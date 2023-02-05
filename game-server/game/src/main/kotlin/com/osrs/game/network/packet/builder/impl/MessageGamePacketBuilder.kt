package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeSmart
import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.MessageGamePacket
import java.nio.ByteBuffer

@Singleton
class MessageGamePacketBuilder : PacketBuilder<MessageGamePacket>(
    opcode = 6,
    size = -1
) {
    override fun build(packet: MessageGamePacket, buffer: ByteBuffer) {
        buffer.writeSmart(packet.type   )
        buffer.writeByte(if (packet.hasPrefix) 1 else 0)
        if (packet.hasPrefix) buffer.writeStringCp1252NullTerminated(packet.prefix)
        buffer.writeStringCp1252NullTerminated(packet.message)
    }
}
