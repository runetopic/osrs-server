package com.osrs.game.network.codec.impl

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.Cache
import com.osrs.database.account.AccountService
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session
import com.osrs.game.network.SessionRequestOpcode.LOGIN_NORMAL_OPCODE
import com.osrs.game.network.SessionRequestOpcode.LOGIN_RECONNECTING_OPCODE
import com.osrs.game.network.SessionResponseOpcode.BAD_SESSION_OPCODE
import com.osrs.game.network.SessionResponseOpcode.CLIENT_OUTDATED_OPCODE
import com.osrs.game.network.SessionResponseOpcode.INVALID_USERNAME_PASSWORD_OPCODE
import com.osrs.game.network.SessionResponseOpcode.LOGIN_SUCCESS_OPCODE
import com.osrs.game.network.codec.CodecChannelHandler
import com.osrs.game.world.World
import com.runetopic.cryptography.fromXTEA
import com.runetopic.cryptography.toISAAC
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import xlitekt.shared.buffer.readInt
import xlitekt.shared.buffer.readIntLittleEndian
import xlitekt.shared.buffer.readIntV1
import xlitekt.shared.buffer.readIntV2
import xlitekt.shared.buffer.readStringCp1252NullCircumfixed
import xlitekt.shared.buffer.readStringCp1252NullTerminated
import xlitekt.shared.buffer.readUByte
import xlitekt.shared.buffer.readUMedium
import xlitekt.shared.buffer.readUShort
import java.math.BigInteger
import java.nio.ByteBuffer

class LoginCodec @Inject constructor(
    private val cache: Cache,
    environment: ApplicationEnvironment,
    val world: World,
    private val accountService: AccountService
) : CodecChannelHandler {
    private val logger = InlineLogger()

    private val buildMajor = environment.config.property("game.build.major").getString().toInt()
    private val rsaExponent = environment.config.property("game.rsa.exponent").getString()
    private val rsaModulus = environment.config.property("game.rsa.modulus").getString()

    override suspend fun handle(session: Session, readChannel: ByteReadChannel, writeChannel: ByteWriteChannel) {
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

        readChannel.discard(3) // Discarding 3 unknown bytes

        when (opcode) {
            LOGIN_NORMAL_OPCODE, LOGIN_RECONNECTING_OPCODE -> {
                val rsaBuffer = ByteArray(readChannel.readShort().toInt() and 0xFFFF)

                if (rsaBuffer.size != readChannel.readAvailable(rsaBuffer, 0, rsaBuffer.size)) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Session RSA buffer is not correct. Disconnecting.")
                    return
                }

                val rsaBlock = ByteBuffer.wrap(BigInteger(rsaBuffer).modPow(BigInteger(rsaExponent, 16), BigInteger(rsaModulus, 16)).toByteArray())

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

                var reconnectXteas: IntArray? = null
                var password: String = ""

                if (opcode == LOGIN_RECONNECTING_OPCODE) {
                    reconnectXteas = IntArray(4) { rsaBlock.readInt() }
                } else {
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

                    password = rsaBlock.readStringCp1252NullTerminated()
                }

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

                readMachineInfo(session, xteaBuffer)

                xteaBuffer.get().toInt() and 0xFF // Client type

                if (xteaBuffer.int != 0) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Xtea buffer is not valid. Disconnecting client")
                    return
                }

                validateClientCRCs(session, xteaBuffer)
                val serverKeys = IntArray(clientKeys.size) { clientKeys[it] + 50 }
                session.setIsaacCiphers(clientKeys.toISAAC(), serverKeys.toISAAC())
                logger.info { "Finished decoding login for $username. Display type: $displayType Canvas width: $canvasWidth Canvas height: $canvasHeight" }

                if (opcode != LOGIN_RECONNECTING_OPCODE && !accountService.validateCredentials(username, password)) {
                    session.writeAndFlush(INVALID_USERNAME_PASSWORD_OPCODE)
                    session.disconnect("Invalid user credentials. Disconnecting client")
                    return
                }

                val player = Player(username, world, session) // TODO load account profiles from the database
                world.requestLogin(session, player)
                session.writeAndFlush(LOGIN_SUCCESS_OPCODE)
                session.setCodec(GameCodec::class)
            }
        }
    }

    private suspend fun readMachineInfo(session: Session, xteaBuffer: ByteBuffer) {
        // TODO Identify all of the machine info and actually parse it into something usable.
        val unknownByte1 = xteaBuffer.readUByte()

        if (unknownByte1 != 9) {
            session.writeAndFlush(BAD_SESSION_OPCODE)
            return session.disconnect("Xtea buffer is invalid.")
        }

        val unknownByte2 = xteaBuffer.readUByte()
        val unknownByte3 = xteaBuffer.readUByte()
        val unknownShort1 = xteaBuffer.readUShort()
        val unknownByte4 = xteaBuffer.readUByte()
        val unknownByte5 = xteaBuffer.readUByte()
        val unknownByte6 = xteaBuffer.readUByte()
        val unknownByte7 = xteaBuffer.readUByte()
        val unknownByte8 = xteaBuffer.readUByte()

        logger.debug { "Unknown byte 1 = $unknownByte2" }
        logger.debug { "Unknown byte 2 = $unknownByte3" }
        logger.debug { "Unknown short 1 = $unknownShort1" }
        logger.debug { "Unknown byte 4 $unknownByte4" }
        logger.debug { "Unknown byte 5 $unknownByte5" }
        logger.debug { "Unknown byte 6 $unknownByte6" }
        logger.debug { "Unknown byte 7 $unknownByte7" }
        logger.debug { "Unknown byte 8 $unknownByte8" }

        val unknownShort2 = xteaBuffer.readUShort()

        logger.debug { "Unknown short 2 $unknownShort2" }

        val unknownByte9 = xteaBuffer.readUByte()

        logger.debug { "Unknown byte 9 $unknownByte9" }

        val unknownMedium = xteaBuffer.readUMedium()

        logger.debug { "Unknown medium $unknownMedium" }

        val unknownShort3 = xteaBuffer.readUShort()

        logger.debug { "Unknown short 3 $unknownShort3" }

        val unknownString1 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 1 $unknownString1" }

        val unknownString2 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 2 $unknownString2" }

        val unknownString3 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 3 $unknownString3" }

        val unknownString4 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 4 $unknownString4" }

        val unknownByte10 = xteaBuffer.readUByte()

        logger.debug { "Unknown byte 10 $unknownByte10" }

        val unknownShort4 = xteaBuffer.readUShort()

        logger.debug { "Unknown short 4 $unknownShort4" }

        val unknownString5 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 5 $unknownString5" }

        val unknownString6 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 6 $unknownString6" }

        val unknownByte11 = xteaBuffer.readUByte()

        logger.debug { "Unknown byte 11 $unknownByte11" }

        val unknownByte12 = xteaBuffer.readUByte()

        logger.debug { "Unknown byte 12 $unknownByte12" }

        val unknownIntArray = IntArray(3) { xteaBuffer.readInt() }

        logger.debug { "Unknown int array ${unknownIntArray.contentToString()}" }

        val unknownInt = xteaBuffer.readInt()

        logger.debug { "Unknown int $unknownInt" }

        val unknownString7 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 7 $unknownString7" }

        val unknownString8 = xteaBuffer.readStringCp1252NullCircumfixed()

        logger.debug { "Unknown string 8 $unknownString8" }
    }

    private suspend fun validateClientCRCs(session: Session, xteaBuffer: ByteBuffer) {
        val clientCRCs = IntArray(cache.validIndexCount()) { -1 }

        clientCRCs[3] = xteaBuffer.int
        clientCRCs[12] = xteaBuffer.readIntV1()
        clientCRCs[17] = xteaBuffer.readIntV1()
        clientCRCs[15] = xteaBuffer.readIntLittleEndian()
        clientCRCs[14] = xteaBuffer.readIntV1()
        clientCRCs[2] = xteaBuffer.readIntLittleEndian()
        xteaBuffer.readIntV1()
        clientCRCs[8] = xteaBuffer.readIntV2()
        clientCRCs[6] = xteaBuffer.readIntLittleEndian()
        clientCRCs[13] = xteaBuffer.int
        clientCRCs[18] = xteaBuffer.readIntV1()
        clientCRCs[9] = xteaBuffer.int
        clientCRCs[7] = xteaBuffer.readIntV2()
        clientCRCs[1] = xteaBuffer.int
        clientCRCs[19] = xteaBuffer.int
        clientCRCs[20] = xteaBuffer.readIntV2()
        clientCRCs[11] = xteaBuffer.int
        clientCRCs[10] = xteaBuffer.readIntLittleEndian()
        clientCRCs[5] = xteaBuffer.readIntLittleEndian()
        clientCRCs[4] = xteaBuffer.int
        clientCRCs[0] = xteaBuffer.readIntV2()

        cache.crcs.forEachIndexed { index, i ->
            if (index == 16 || index == 21) return@forEachIndexed // Client is skipping index 16 and 21
            if (clientCRCs[index] != i) {
                logger.info { "Client crc $index ${clientCRCs[index]} != ${cache.crcs[index]}" }
                session.writeAndFlush(CLIENT_OUTDATED_OPCODE)
                return session.disconnect("Bad Session. Client and cache crc are mismatched.")
            }
        }
    }
}
