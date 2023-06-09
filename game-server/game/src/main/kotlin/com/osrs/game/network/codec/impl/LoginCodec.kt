package com.osrs.game.network.codec.impl

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.Cache
import com.osrs.api.buffer.RSByteBuffer
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
import com.osrs.service.account.AccountService
import com.runetopic.cryptography.fromXTEA
import com.runetopic.cryptography.toISAAC
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import java.math.BigInteger

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

                val rsaBlock = RSByteBuffer(
                    BigInteger(rsaBuffer).modPow(
                        BigInteger(rsaExponent, 16),
                        BigInteger(rsaModulus, 16)
                    ).toByteArray()
                )

                if (rsaBlock.readByte() != 1) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Session RSA match key was not 1. Disconnecting.")
                    return
                }

                val clientKeys = IntArray(4) { rsaBlock.readInt() }
                val clientSeed = rsaBlock.readLong()

                if (clientSeed != session.seed()) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Server/Client seed miss-match. Disconnecting.")
                    return
                }

                val reconnecting = opcode == LOGIN_RECONNECTING_OPCODE

                var reconnectXteas: IntArray? = null
                var password = ""

                if (reconnecting) {
                    reconnectXteas = IntArray(4) { rsaBlock.readInt() }
                    // TODO verify reconnect xteas from previous request
                } else {
                    // TODO: implement the different authentication types and reconnection
                    when (val authenticationType = rsaBlock.readByte()) {
                        1, 2 -> rsaBlock.discard(4)
                        0, 3 -> rsaBlock.discard(3)
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

                val xteaBuffer = RSByteBuffer(availableBuffer.fromXTEA(32, clientKeys))

                val username = xteaBuffer.readStringCp1252NullTerminated()
                val displayType = xteaBuffer.readByte()
                val canvasWidth = xteaBuffer.readUShort()
                val canvasHeight = xteaBuffer.readUShort()
                xteaBuffer.position(xteaBuffer.position() + 24) // Skipping random bytes generated by the client
                xteaBuffer.readStringCp1252NullTerminated() // Client token
                xteaBuffer.discard(4)

                readMachineInfo(session, xteaBuffer)

                xteaBuffer.discard(1) // Client type

                if (xteaBuffer.readInt() != 0) {
                    session.writeAndFlush(BAD_SESSION_OPCODE)
                    session.disconnect("Xtea buffer is not valid. Disconnecting client")
                    return
                }

                validateClientCRCs(session, xteaBuffer)
                val serverKeys = IntArray(clientKeys.size) { clientKeys[it] + 50 }
                session.setIsaacCiphers(clientKeys.toISAAC(), serverKeys.toISAAC())
                logger.info { "Finished decoding login for $username. Display type: $displayType Canvas width: $canvasWidth Canvas height: $canvasHeight" }

                val account = accountService.findAccountByUsername(username)

                if (account == null || !reconnecting && !accountService.validateAccount(password, account.password)) {
                    session.writeAndFlush(INVALID_USERNAME_PASSWORD_OPCODE)
                    session.disconnect("Invalid user credentials. Disconnecting client")
                    return
                }

                world.requestLogin(
                    Player(
                        account = account,
                        world = world,
                        session = session
                    )
                )

                session.writeAndFlush(LOGIN_SUCCESS_OPCODE)
                session.setCodec(GameCodec::class)
            }
        }
    }

    private suspend fun readMachineInfo(session: Session, xteaBuffer: RSByteBuffer) {
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

        val unknownMedium = xteaBuffer.readU24BitInt()

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

    private suspend fun validateClientCRCs(session: Session, xteaBuffer: RSByteBuffer) {
        val clientCRCs = IntArray(cache.validIndexCount()) { -1 }
        clientCRCs[12] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[2] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[10] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[11] = xteaBuffer.readIntLittleEndian()
        clientCRCs[5] = xteaBuffer.readIntMiddleEndian()
        xteaBuffer.readIntLittleEndian() // Always 0
        clientCRCs[14] = xteaBuffer.readIntLittleEndian()
        clientCRCs[19] = xteaBuffer.readInt()
        clientCRCs[18] = xteaBuffer.readIntMiddleEndian()
        clientCRCs[20] = xteaBuffer.readInt()
        clientCRCs[0] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[3] = xteaBuffer.readIntLittleEndian()
        clientCRCs[8] = xteaBuffer.readIntMiddleEndian()
        clientCRCs[13] = xteaBuffer.readInt()
        clientCRCs[6] = xteaBuffer.readIntMiddleEndian()
        clientCRCs[15] = xteaBuffer.readIntMiddleEndian()
        clientCRCs[9] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[7] = xteaBuffer.readIntLittleMiddleEndian()
        clientCRCs[17] = xteaBuffer.readIntLittleEndian()
        clientCRCs[4] = xteaBuffer.readInt()
        clientCRCs[1] = xteaBuffer.readIntLittleEndian()
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
