package com.osrs.network.codec.impl

import com.github.michaelbull.logging.InlineLogger
import com.osrs.cache.Cache
import com.osrs.network.Session
import com.osrs.network.SessionRequestOpcode.LOGIN_NORMAL_OPCODE
import com.osrs.network.SessionResponseOpcode.BAD_SESSION_OPCODE
import com.osrs.network.SessionResponseOpcode.CLIENT_OUTDATED_OPCODE
import com.osrs.network.SessionResponseOpcode.LOGIN_SUCCESS_OPCODE
import com.osrs.network.codec.ByteChannelCodec
import com.osrs.network.io.readIntLittleEndian
import com.osrs.network.io.readIntV1
import com.osrs.network.io.readIntV2
import com.osrs.network.io.readStringCp1252NullTerminated
import com.runetopic.cryptography.fromXTEA
import com.runetopic.cryptography.toISAAC
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import java.math.BigInteger
import java.nio.ByteBuffer

class LoginCodec(
    private val session: Session,
    private val cache: Cache,
    environment: ApplicationEnvironment
) : ByteChannelCodec {
    private val logger = InlineLogger()

    private val buildMajor = environment.config.property("game.build.major").getString().toInt()
    private val rsaExponent = environment.config.property("game.rsa.exponent").getString()
    private val rsaModulus = environment.config.property("game.rsa.modulus").getString()

    override suspend fun handle(readChannel: ByteReadChannel, writeChannel: ByteWriteChannel) {
        val opcode = readChannel.readByte().toInt() and 0xFF
        val size = readChannel.readShort().toInt() and 0xFFFF
        val available = readChannel.availableForRead

        if (size != available) {
            session.writeAndFlush(BAD_SESSION_OPCODE)
            session.disconnect("Bad session. Login buffer size: $size Available bytes to read: $available")
            return
        }

        val clientBuild = readChannel.readInt()
        val clientSubBuild = readChannel.readInt()

        if (clientBuild != buildMajor) {
            session.writeAndFlush(CLIENT_OUTDATED_OPCODE)
            session.disconnect("Session is using an outdated client. Client build does not match server build. Disconnecting.")
            return
        }

        if (clientSubBuild != 1) { // pretty much always 1 here, but it might be worth setting the minor version in our application.conf
            session.writeAndFlush(CLIENT_OUTDATED_OPCODE)
            session.disconnect("Session is using an outdated client. Client sub build does not match server sub build. Disconnecting.")
            return
        }

        logger.info { "Incoming login opcode $opcode with client build $clientBuild.$clientSubBuild" }

        readChannel.discard(3) // Discarding 3 unknown byte

        when (opcode) {
            LOGIN_NORMAL_OPCODE -> {
                val rsaBuffer = ByteArray(readChannel.readShort().toInt() and 0xFFFF)

                if (rsaBuffer.size != readChannel.readAvailable(rsaBuffer, 0, rsaBuffer.size)) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Session RSA buffer is not correct. Disconnecting.")
                    return
                }

                val rsaBlock = ByteBuffer.wrap(BigInteger(rsaBuffer).modPow(BigInteger(rsaExponent), BigInteger(rsaModulus)).toByteArray())

                if (rsaBlock.get().toInt() != 1) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Session RSA match key was not 1. Disconnecting.")
                    return
                }

                val clientKeys = IntArray(4) { rsaBlock.int }
                val clientSeed = rsaBlock.long

                if (clientSeed != session.seed()) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Server/Client seed miss-match. Disconnecting.")
                    return
                }

                // TODO: implement the different authentication types and reconnection
                when (val authenticationType = rsaBlock.get().toInt()) {
                    1, 2 -> rsaBlock.position(rsaBlock.position() + 4)
                    0, 3 -> rsaBlock.position(rsaBlock.position() + 3)
                    else -> {
                        session.writeAndFlush(BAD_SESSION_OPCODE)
                        session.disconnect("Unknown authentication type $authenticationType Disconnecting.")
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
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Xtea buffer is not valid. Disconnecting client")
                    return
                }

                validateClientCRCs(xteaBuffer)
                val serverKeys = IntArray(clientKeys.size) { clientKeys[it] + 50 }
                session.setIsaacCiphers(clientKeys.toISAAC(), serverKeys.toISAAC())
                logger.info { "Finished decoding login for $username. Display type: $displayType Canvas width: $canvasWidth Canvas height: $canvasHeight" }
                writeChannel.writeLoginAndFlush(LOGIN_SUCCESS_OPCODE)
                session.setCodec(::GameCodec)
            }
        }
    }

    private fun readMachineInfo(xteaBuffer: ByteBuffer) {
        // TODO Identify all of the machine info and actually parse it into something usable.
        xteaBuffer.position(xteaBuffer.position() + 55)
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
                session.writeAndFlush(CLIENT_OUTDATED_OPCODE)
                return session.disconnect("Bad Session. Client and cache crc are mismatched.")
            }
        }
    }

    private suspend fun ByteWriteChannel.writeLoginAndFlush(response: Int) {
        session.writeAndFlush(response)
        writeByte(11)
        writeByte(0)
        writeInt(0)
        writeByte(2) // Rights
        writeByte(0)
        writeShort(1) // Index
        writeByte(0)
        flush()
    }
}
