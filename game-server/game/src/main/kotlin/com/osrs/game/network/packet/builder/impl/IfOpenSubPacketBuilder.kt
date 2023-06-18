package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfOpenSubPacket

@Singleton
class IfOpenSubPacketBuilder : PacketBuilder<IfOpenSubPacket>(
    opcode = 89,
    size = 7
) {
    override fun build(packet: IfOpenSubPacket, buffer: RSByteBuffer) {
        buffer.writeShortLittleEndian(packet.interfaceId)
        buffer.writeByteAdd(if (!packet.isModal) 1 else 0)
        buffer.writeIntLittleMiddleEndian(packet.toInterface)
    }
}
