package com.osrs.network.session

import com.github.michaelbull.logging.InlineLogger
import com.osrs.cache.Cache
import com.osrs.network.io.readIntLittleEndian
import com.osrs.network.io.readIntV1
import com.osrs.network.io.readIntV2
import com.osrs.network.io.readPacketOpcode
import com.osrs.network.io.readPacketSize
import com.osrs.network.io.readStringCp1252NullTerminated
import com.osrs.network.io.readUMedium
import com.runetopic.cryptography.fromXTEA
import com.runetopic.cryptography.isaac.ISAAC
import com.runetopic.cryptography.toISAAC
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

    private lateinit var clientCipher: ISAAC
    private lateinit var serverCipher: ISAAC

    private val seed = ((Math.random() * 99999999.0).toLong() shl 32) + (Math.random() * 99999999.0).toLong()

    private val serverBuild = environment.config.property("game.build.major").getString().toInt()
    private val packetSizes = environment.config.property("game.packet.sizes").getList().map(String::toInt)
    private val exponent = environment.config.property("game.rsa.exponent").getString()
    private val modulus = environment.config.property("game.rsa.modulus").getString()

    suspend fun connect() = when (val opcode = readChannel.readByte().toInt()) {
        HANDSHAKE_JS5_OPCODE, HANDSHAKE_LOGIN_OPCODE -> preformHandshake(opcode)
        else -> {
            logger.info { "Unhandled opcode $opcode" }
        }
    }

    private suspend fun preformHandshake(opcode: Int) {
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

                val rsaBlock = ByteBuffer.wrap(BigInteger(rsaBuffer).modPow(BigInteger(exponent), BigInteger(modulus)).toByteArray())

                if (rsaBlock.get().toInt() != 1) {
                    writeAndFLush(BAD_SESSION_OPCODE)
                    disconnect("Session RSA match key was not 1. Disconnecting.")
                    return
                }

                val clientKeys = IntArray(4) { rsaBlock.int }
                val clientSeed = rsaBlock.long

                if (clientSeed != seed) {
                    writeAndFLush(BAD_SESSION_OPCODE)
                    disconnect("Server/Client seed miss-match. Disconnecting.")
                    return
                }

                // TODO: implement the different authentication types and reconnection
                when (val authenticationType = rsaBlock.get().toInt()) {
                    1, 2 -> rsaBlock.position(rsaBlock.position() + 4)
                    0, 3 -> rsaBlock.position(rsaBlock.position() + 3)
                    else -> {
                        writeAndFLush(BAD_SESSION_OPCODE)
                        disconnect("Unknown authentication type $authenticationType Disconnecting.")
                        return
                    }
                }
                // Password type? Not entirely sure
                rsaBlock.position(rsaBlock.position() + 1)
                val password = rsaBlock.readStringCp1252NullTerminated()

                val availableBuffer = ByteArray(readChannel.availableForRead)
                readChannel.readAvailable(availableBuffer, 0, availableBuffer.size)

                val xteaBuffer = ByteBuffer.wrap(availableBuffer.fromXTEA(32, clientKeys))

                val username = xteaBuffer.readStringCp1252NullTerminated()
                val displayType = xteaBuffer.get().toInt()
                val canvasWidth = xteaBuffer.short.toInt() and 0xFFFF
                val canvasHeight = xteaBuffer.short.toInt() and 0xFFFF
                xteaBuffer.position(xteaBuffer.position() + 24) // Skipping random bytes generated by the client
                xteaBuffer.readStringCp1252NullTerminated() // Client token
                xteaBuffer.int

                readMachineInfo(xteaBuffer)

                xteaBuffer.get().toInt() and 0xFF // Client type

                if (xteaBuffer.int != 0) {
                    writeAndFLush(BAD_SESSION_OPCODE)
                    disconnect("Xtea buffer is not valid. Disconnecting client")
                    return
                }

                validateClientCRCs(xteaBuffer)
                val serverKeys = IntArray(clientKeys.size) { clientKeys[it] + 50 }
                setIsaacCiphers(clientKeys.toISAAC(), serverKeys.toISAAC())
                logger.info { "Finished decoding login for $username. Display type: $displayType Canvas width: $canvasWidth Canvas height: $canvasHeight" }
                writeLoginAndFlush(2)
            }
        }
        logger.info { "Incoming login opcode $opcode with client build $clientBuild.$clientSubBuild" }
    }

    private suspend fun validateClientCRCs(xteaBuffer: ByteBuffer) {
        val clientCRCs = IntArray(cache.validIndexCount()) { -1 }

        clientCRCs[13] = xteaBuffer.readIntV2()
        clientCRCs[2] = xteaBuffer.int
        clientCRCs[19] = xteaBuffer.readIntV1()
        clientCRCs[8] = xteaBuffer.int
        clientCRCs[5] = xteaBuffer.readIntV2()
        xteaBuffer.readIntV1()
        clientCRCs[1] = xteaBuffer.readIntLittleEndian()
        clientCRCs[15] = xteaBuffer.readIntV1()
        clientCRCs[10] = xteaBuffer.int
        clientCRCs[0] = xteaBuffer.readIntV2()
        clientCRCs[18] = xteaBuffer.readIntLittleEndian()
        clientCRCs[6] = xteaBuffer.readIntV2()
        clientCRCs[3] = xteaBuffer.readIntV1()
        clientCRCs[11] = xteaBuffer.readIntLittleEndian()
        clientCRCs[7] = xteaBuffer.readIntV1()
        clientCRCs[9] = xteaBuffer.int
        clientCRCs[14] = xteaBuffer.readIntLittleEndian()
        clientCRCs[17] = xteaBuffer.readIntLittleEndian()
        clientCRCs[20] = xteaBuffer.readIntV1()
        clientCRCs[4] = xteaBuffer.int
        clientCRCs[12] = xteaBuffer.readIntLittleEndian()

        cache.crcs.forEachIndexed { index, i ->
            if (index == 16 || index == 21) return@forEachIndexed // Client is skipping index 16 and 21
            if (clientCRCs[index] != i) {
                logger.info { "Client crc $index ${clientCRCs[index]} != ${cache.crcs[index]}" }
                writeAndFLush(CLIENT_OUTDATED_OPCODE)
                return disconnect("Bad Session. Client and cache crc are mismatched.")
            }
        }
    }

    private fun readMachineInfo(xteaBuffer: ByteBuffer) {
        // TODO Identify all of the machine info and actually parse it into something usable.
        xteaBuffer.position(xteaBuffer.position() + 55)
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

    private fun setIsaacCiphers(clientCipher: ISAAC, serverCipher: ISAAC) {
        this.clientCipher = clientCipher
        this.serverCipher = serverCipher
    }

    private suspend fun writeLoginAndFlush(response: Int) {
        writeAndFLush(response)
        writeChannel.apply {
            writeByte(11)
            writeByte(0)
            writeInt(0)
            writeByte(2) // Rights
            writeByte(0)
            writeShort(1) // Index
            writeByte(0)
        }.flush()
        readPackets()
    }

    private suspend fun readPackets() = coroutineScope {
        try {
            while (this.isActive) {
                if (readChannel.isClosedForRead) break
                val opcode = readChannel.readPacketOpcode(clientCipher)
                if (opcode < 0 || opcode >= packetSizes.size) continue
                val size = readChannel.readPacketSize(packetSizes[opcode])
                logger.info { "Incoming client packet. Opcode: $opcode." }
                readChannel.discard(size.toLong())
                continue
            }
        } catch (exception: Exception) {
            handleSessionException(exception)
        }
    }

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

    private suspend fun writeAndFLush(opcode: Int) {
        writeChannel.writeByte(opcode.toByte())
        writeChannel.flush()
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
