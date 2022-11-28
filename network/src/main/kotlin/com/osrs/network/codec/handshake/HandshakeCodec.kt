package com.osrs.network.codec.handshake

import com.osrs.network.Session
import com.osrs.network.SessionRequestOpcode.HANDSHAKE_JS5_OPCODE
import com.osrs.network.SessionRequestOpcode.HANDSHAKE_LOGIN_OPCODE
import com.osrs.network.SessionResponseOpcode.CLIENT_OUTDATED_OPCODE
import com.osrs.network.SessionResponseOpcode.HANDSHAKE_SUCCESS_OPCODE
import com.osrs.network.codec.CodecChannelHandler
import com.osrs.network.codec.login.LoginCodec
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

class HandshakeCodec(
    environment: ApplicationEnvironment
) : CodecChannelHandler {
    private val buildMajor = environment.config.property("game.build.major").getString().toInt()

    override suspend fun handle(session: Session, readChannel: ByteReadChannel, writeChannel: ByteWriteChannel) {
        return when (val opcode = readChannel.readByte().toInt()) {
            HANDSHAKE_JS5_OPCODE -> {
                if (readChannel.readInt() != buildMajor) {
                    session.writeAndFlush(CLIENT_OUTDATED_OPCODE)
                    session.disconnect("Session has an invalid client build.")
                    return
                }
                session.writeAndFlush(HANDSHAKE_SUCCESS_OPCODE)
                session.setCodec(LoginCodec::class)
            }
            HANDSHAKE_LOGIN_OPCODE -> {
                session.writeAndFlush(HANDSHAKE_SUCCESS_OPCODE)
                writeChannel.apply { writeLong(session.seed()) }.flush()
                session.setCodec(LoginCodec::class)
            }
            else -> {
                session.disconnect("Unhandled opcode during handshake. Opcode = $opcode")
            }
        }
    }
}
