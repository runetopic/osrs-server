package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.readUByte
import com.osrs.api.buffer.readUShortSmart
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
        val type = readChannel.readUByte()
        return PublicChatPacket(
            unknown1 = type,
            color = readChannel.readUByte(),
            effect = readChannel.readUByte(),
            decompressedSize = readChannel.readUShortSmart(),
            compressedBytes = readChannel.readPacket(size - (mark - (readChannel.availableForRead - if (type == 3) 1 else 0))).readBytes(),
            unknown2 = if (type == 3) readChannel.readUByte() else -1
        )
    }
}
