package com.osrs.game.network.packet.builder.impl

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfSetHidePacket
import com.osrs.game.network.packet.type.server.IfSetPositionPacket
import com.osrs.game.network.packet.type.server.IfSetTextPacket

class IfSetPositionPacketBuilder : PacketBuilder<IfSetPositionPacket>(
    opcode = 40,
    size = 8
) {
    override fun build(packet: IfSetPositionPacket, buffer: RSByteBuffer) {
        buffer.writeIntLittleEndian(packet.packed)
        buffer.writeShortLittleEndian(packet.rawX)
        buffer.writeShortLittleEndian(packet.rawY)
    }
}
