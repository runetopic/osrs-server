package com.osrs.network.session

import com.github.michaelbull.logging.InlineLogger
import com.osrs.cache.Cache
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.util.reflect.instanceOf
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ClosedWriteChannelException
import io.ktor.utils.io.close
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.BigInteger
import java.net.SocketException
import java.nio.ByteBuffer

class Session constructor(
    private val socket: Socket,
    environment: ApplicationEnvironment,
    private val cache: Cache
) {
    private val logger = InlineLogger()

    private val readChannel = socket.openReadChannel()
    private val writeChannel = socket.openWriteChannel()

    private val seed = ((Math.random() * 99999999.0).toLong() shl 32) + (Math.random() * 99999999.0).toLong()

    private val serverBuild = environment.config.property("game.build.major").getString().toInt()
    private val exponent = environment.config.property("game.rsa.exponent").getString()
    private val modulus = environment.config.property("game.rsa.modulus").getString()

    suspend fun connect() = when (val opcode = readChannel.readByte().toInt()) {
        HANDSHAKE_JS5_OPCODE, HANDSHAKE_LOGIN_OPCODE -> preformHandshake(opcode)
        else -> {
            logger.info { "Unhandled opcode $opcode" }
        }
    }

    private suspend fun preformHandshake(opcode: Int) {
        logger.info { "Preforming handshake with opcode: $opcode" }
        when (opcode) {
            HANDSHAKE_JS5_OPCODE -> {
                if (readChannel.readInt() != serverBuild) {
                    withContext(Dispatchers.IO) {
                        writeAndFLush(CLIENT_OUTDATED_OPCODE)
                        disconnect("Session has an invalid client build.")
                    }
                    return
                }
                writeAndFLush(HANDSHAKE_SUCCESS_OPCODE)
                readJS5Request(readChannel)
            }
            HANDSHAKE_LOGIN_OPCODE -> {
                writeAndFLush(HANDSHAKE_SUCCESS_OPCODE)
                writeChannel.apply { writeLong(seed) }.flush()
                readLogin()
            }
        }
    }

    private suspend fun readLogin() {
        val opcode = readChannel.readByte().toInt() and 0xFF
        val size = readChannel.readShort().toInt() and 0xFFFF
        val available = readChannel.availableForRead

        if (size != available) {
            writeAndFLush(BAD_SESSION_OPCODE)
            disconnect("Bad session. Login buffer size: $size Available bytes to read: $available")
            return
        }

        val clientBuild = readChannel.readInt()
        val clientSubBuild = readChannel.readInt()

        if (clientBuild != serverBuild) {
            writeAndFLush(CLIENT_OUTDATED_OPCODE)
            disconnect("Session is using an outdated client. Client build does not match server build. Disconnecting.")
            return
        }

        if (clientSubBuild != 1) { // pretty much always 1 here, but it might be worth setting the minor version in our application.conf
            writeAndFLush(CLIENT_OUTDATED_OPCODE)
            disconnect("Session is using an outdated client. Client sub build does not match server sub build. Disconnecting.")
            return
        }

        readChannel.discard(3) // Discarding 3 unknown byte

        when (opcode) {
            LOGIN_NORMAL_OPCODE -> {
                val rsaBuffer = ByteArray(readChannel.readShort().toInt() and 0xFFFF)

                if (rsaBuffer.size != readChannel.readAvailable(rsaBuffer, 0, rsaBuffer.size)) {
                    writeAndFLush(BAD_SESSION_OPCODE)
                    disconnect("Session RSA buffer is not correct. Disconnecting.")
                    return
                }

                logger.info { "Exponent $exponent" }
                logger.info { "Modulus $modulus" }
                val rsaBlock = ByteBuffer.wrap(BigInteger(rsaBuffer).modPow(BigInteger(exponent, 16), BigInteger(modulus, 16)).toByteArray())

                if (rsaBlock.readByte() != 1) {
                    writeAndFLush(BAD_SESSION_OPCODE)
                    disconnect("Session RSA match key was not 1. Disconnecting.")
                    return
                }
            }
        }
        logger.info { "Incoming login opcode $opcode with client build $clientBuild.$clientSubBuild" }
    }

    private suspend fun writeAndFLush(opcode: Int) {
        writeChannel.writeByte(opcode.toByte())
        writeChannel.flush()
    }

    private suspend fun readJS5Request(readChannel: ByteReadChannel) = coroutineScope {
        try {
            while (this.isActive) {
                when (val opcode = readChannel.readByte().toInt()) {
                    JS5_HIGH_PRIORITY_OPCODE, JS5_LOW_PRIORITY_OPCODE -> {
                        val uid = readChannel.readUMedium()
                        val indexId = uid shr 16
                        val groupId = uid and 0xFFFF
                        val masterRequest = indexId == 0xFF && groupId == 0xFF
                        ByteBuffer.wrap(if (masterRequest) cache.checksums else cache.groupReferenceTable(indexId, groupId)).apply {
                            if (capacity() == 0 || limit() == 0) return@coroutineScope
                            val compression = if (masterRequest) 0 else get().toInt() and 0xff
                            val size = if (masterRequest) cache.checksums.size else int
                            writeJS5File(indexId, groupId, compression, size, this)
                        }
                    }
                    JS5_ENCRYPTION_OPCODE, JS5_SWITCH_OPCODE, JS5_LOGGED_IN_OPCODE -> readChannel.discard(3)
                    else -> throw IllegalStateException("Unhandled Js5 opcode. Opcode: $opcode")
                }
            }
        } catch (exception: Exception) {
            handleSessionException(exception)
        }
    }

    private suspend fun writeJS5File(indexId: Int, groupId: Int, compression: Int, size: Int, buffer: ByteBuffer) = writeChannel.apply {
        writeByte(indexId.toByte())
        writeShort(groupId.toShort())
        writeByte(compression.toByte())
        writeInt(size)

        var writeOffset = 8
        repeat(if (compression != 0) size + 4 else size) {
            if (writeOffset % 512 == 0) {
                writeByte(0xff.toByte())
                writeOffset = 1
            }
            writeByte(buffer[it + buffer.position()])
            writeOffset++
        }
    }.flush()

    private fun handleSessionException(exception: Exception) = when {
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

    private fun disconnect(reason: String) {
        logger.info { "Session has been disconnected for reason={$reason}." }
        writeChannel.close()
        socket.close()
    }

    companion object {
        const val HANDSHAKE_SUCCESS_OPCODE = 0
        const val JS5_HIGH_PRIORITY_OPCODE = 0
        const val JS5_LOW_PRIORITY_OPCODE = 1
        const val JS5_LOGGED_IN_OPCODE = 2
        const val JS5_SWITCH_OPCODE = 3
        const val JS5_ENCRYPTION_OPCODE = 4
        const val CLIENT_OUTDATED_OPCODE = 6
        const val HANDSHAKE_LOGIN_OPCODE = 14
        const val HANDSHAKE_JS5_OPCODE = 15
        const val LOGIN_NORMAL_OPCODE = 16

        const val BAD_SESSION_OPCODE = 10
    }
}

fun ByteBuffer.readByte() = get().toInt()
suspend fun ByteReadChannel.readUByte() = readByte().toInt() and 0xff
suspend fun ByteReadChannel.readUShort() = readShort().toInt() and 0xffff
suspend fun ByteReadChannel.readUMedium() = (readUByte() shl 16) or readUShort()
