package com.osrs.network

import com.github.michaelbull.logging.InlineLogger
import com.osrs.network.codec.CodecChannelHandler
import com.osrs.network.codec.impl.HandshakeCodec
import com.osrs.network.packet.Packet
import com.osrs.network.packet.builder.PacketBuilder
import com.runetopic.cryptography.isaac.ISAAC
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.reflect.instanceOf
import io.ktor.utils.io.ClosedWriteChannelException
import io.ktor.utils.io.close
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.runBlocking
import xlitekt.shared.buffer.writeByte
import xlitekt.shared.buffer.writeShort
import java.io.IOException
import java.net.SocketException
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class Session(
    private val socket: Socket,
    private val codecs: Set<CodecChannelHandler>,
    private val builders: Map<KClass<*>, PacketBuilder<Packet>>,
) {
    private val logger = InlineLogger()

    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    private lateinit var clientCipher: ISAAC
    private lateinit var serverCipher: ISAAC
    private var codec: CodecChannelHandler? = codecs.find { it.instanceOf(HandshakeCodec::class) }

    private val seed = ((Math.random() * 99999999.0).toLong() shl 32) + (Math.random() * 99999999.0).toLong()

    private val writePool = ByteBuffer.allocateDirect(40000)

    suspend fun connect() = codec?.handle(this, readChannel, writeChannel)

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

    fun write(packet: Packet) {
        val builder = builders[packet::class] ?: return

        if (builder.opcode > Byte.MAX_VALUE) writePool.writeByte(128 + serverCipher.getNext())

        writePool.writeByte(builder.opcode + serverCipher.getNext() and 0xFF)

        if (builder.size != -1 && builder.size != -2) {
            builder.build(packet, writePool)
            return
        }

        val startPos = writePool.position()
        val offset = startPos + if (builder.size == -1) 1 else 2
        writePool.position(offset)
        builder.build(packet, writePool)
        val endPos = writePool.position()
        val size = endPos - offset
        writePool.position(startPos)
        if (builder.size == -1) writePool.writeByte(size)
        else writePool.writeShort(size)
        writePool.position(endPos)
    }

    suspend fun writeAndFlush(opcode: Int) {
        writeChannel.writeByte(opcode.toByte())
        writeChannel.flush()
    }

    internal fun invokeAndClearWritePool() {
        writeChannel.let {
            if (it.isClosedForWrite) return
            // This way we only have to suspend once per client.
            runBlocking(Dispatchers.IO) {
                it.writeFully(writePool.flip())
            }
            it.flush()
        }
        // Set write pool back to default.
        writePool.clear()
    }

    suspend fun setCodec(codec: KClass<*>) {
        val handledCodec = this.codecs.find { it.instanceOf(codec) } ?: throw IllegalArgumentException("Unhandled codec provided. ${codec.qualifiedName} is not a valid codec. Codecs=${this.codecs}")
        this.codec = handledCodec
        handledCodec.handle(this, readChannel, writeChannel)
    }

    fun seed() = seed

    fun disconnect(reason: String) {
        logger.info { "Session has been disconnected for reason={$reason}." }
        writeChannel.close()
        socket.close()
    }
}
