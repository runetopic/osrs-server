package com.osrs.network.codec.game

import com.github.michaelbull.logging.InlineLogger
import com.osrs.network.Session
import com.osrs.network.codec.CodecChannelHandler
import com.osrs.network.io.readPacketOpcode
import com.osrs.network.io.readPacketSize
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

class GameCodec(
    environment: ApplicationEnvironment
) : CodecChannelHandler {
    private val logger = InlineLogger()
    private val packetSizes = environment.config.property("game.packet.sizes").getList().map(String::toInt)

    override suspend fun handle(session: Session, readChannel: ByteReadChannel, writeChannel: ByteWriteChannel) = coroutineScope {
        val (clientCipher, _) = session.getIsaacCiphers()
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
            session.handleSessionException(exception)
        }
    }
}
