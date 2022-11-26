package com.osrs.network

import com.github.michaelbull.logging.InlineLogger
import com.osrs.cache.Cache
import com.osrs.network.codec.ByteChannelCodec
import com.osrs.network.codec.impl.HandshakeCodec
import com.runetopic.cryptography.isaac.ISAAC
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.util.reflect.instanceOf
import io.ktor.utils.io.ClosedWriteChannelException
import io.ktor.utils.io.close
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.io.IOException
import java.net.SocketException

class Session constructor(
    private val socket: Socket,
    private val cache: Cache,
    private val environment: ApplicationEnvironment,
) {
    private val logger = InlineLogger()

    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    private lateinit var clientCipher: ISAAC
    private lateinit var serverCipher: ISAAC
    private var codec: ByteChannelCodec = HandshakeCodec(this, environment)

    private val seed = ((Math.random() * 99999999.0).toLong() shl 32) + (Math.random() * 99999999.0).toLong()

    suspend fun connect() {
        codec.handle(readChannel, writeChannel)
    }

    fun getIsaacCiphers(): Pair<ISAAC, ISAAC> = this.clientCipher to serverCipher

    fun setIsaacCiphers(clientCipher: ISAAC, serverCipher: ISAAC) {
        this.clientCipher = clientCipher
        this.serverCipher = serverCipher
    }

    fun handleSessionException(exception: Exception) = when {
        exception.instanceOf(TimeoutCancellationException::class) -> disconnect("Client timed out.")
        exception.instanceOf(SocketException::class) -> disconnect("Connection reset.")
        exception.instanceOf(ClosedWriteChannelException::class) -> disconnect("The channel was closed.")
        exception.instanceOf(ClosedReceiveChannelException::class) -> disconnect("The channel was closed.")
        exception.instanceOf(IOException::class) -> disconnect("Client IO exception caught.")
        else -> {
            exception.printStackTrace()
            logger.error(exception) { "Exception caught during client IO Events." }
            disconnect(exception.message.toString())
        }
    }

    suspend fun writeAndFlush(opcode: Int) {
        writeChannel.writeByte(opcode.toByte())
        writeChannel.flush()
    }

    suspend fun <T : ByteChannelCodec> setCodec(function: (Session, Cache) -> T) {
        this.codec = function.invoke(this, cache)
        this.codec.handle(readChannel, writeChannel)
    }

    suspend fun <T : ByteChannelCodec> setCodec(function: (Session, Cache, ApplicationEnvironment) -> T) {
        this.codec = function.invoke(this, cache, environment)
        this.codec.handle(readChannel, writeChannel)
    }

    fun seed() = seed

    fun disconnect(reason: String) {
        logger.info { "Session has been disconnected for reason={$reason}." }
        writeChannel.close()
        socket.close()
    }
}
