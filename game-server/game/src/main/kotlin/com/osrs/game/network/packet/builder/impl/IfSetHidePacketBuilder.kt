package com.osrs.game.network.packet.builder.impl

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfSetHidePacket
import com.osrs.game.network.packet.type.server.IfSetTextPacket

class IfSetHidePacketBuilder : PacketBuilder<IfSetHidePacket>(
    opcode = 102,
    size = 5
) {
    override fun build(packet: IfSetHidePacket, buffer: RSByteBuffer) {
        buffer.writeInt(packet.packed)
        buffer.writeByteAdd(if (packet.hidden) 1 else 0)
    }
}
