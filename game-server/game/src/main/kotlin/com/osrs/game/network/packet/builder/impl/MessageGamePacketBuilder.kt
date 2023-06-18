package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.MessageGamePacket

@Singleton
class MessageGamePacketBuilder : PacketBuilder<MessageGamePacket>(
    opcode = 32,
    size = -1
) {
    override fun build(packet: MessageGamePacket, buffer: RSByteBuffer) {
        buffer.writeSmartByteShort(packet.type)
        buffer.writeByte(if (packet.hasPrefix) 1 else 0)
        if (packet.hasPrefix) buffer.writeStringCp1252NullTerminated(packet.prefix)
        buffer.writeStringCp1252NullTerminated(packet.message)
    }
}
