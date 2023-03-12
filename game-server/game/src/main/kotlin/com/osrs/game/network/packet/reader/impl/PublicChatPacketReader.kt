package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUShortSmart
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.PublicChatPacket
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes

/**
 * @author Jordan Abraham
 */
@Singleton
class PublicChatPacketReader : PacketReader<PublicChatPacket>(
    opcode = 46,
    size = -1
) {
    override suspend fun read(readChannel: ByteReadChannel, size: Int): PublicChatPacket {
        val mark = readChannel.availableForRead
        return PublicChatPacket(
            idk = readChannel.readUByte(),
            color = readChannel.readUByte(),
            effect = readChannel.readUByte(),
            length = readChannel.readUShortSmart(),
            bytes = readChannel.readPacket(size - (mark - readChannel.availableForRead)).readBytes()
        )
    }
}
