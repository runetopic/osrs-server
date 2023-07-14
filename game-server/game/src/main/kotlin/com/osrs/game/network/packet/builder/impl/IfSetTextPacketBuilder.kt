package com.osrs.game.network.packet.builder.impl

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfSetTextPacket

class IfSetTextPacketBuilder : PacketBuilder<IfSetTextPacket>(
    opcode = 83,
    size = -2
) {
    override fun build(packet: IfSetTextPacket, buffer: RSByteBuffer) {
        buffer.writeStringCp1252NullTerminated(packet.text)
        buffer.writeIntMiddleEndian(packet.packed)
    }
}
