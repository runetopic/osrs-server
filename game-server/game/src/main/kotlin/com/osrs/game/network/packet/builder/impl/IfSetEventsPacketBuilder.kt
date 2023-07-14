package com.osrs.game.network.packet.builder.impl

import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfSetEventsPacket
import com.osrs.game.network.packet.type.server.IfSetTextPacket

class IfSetEventsPacketBuilder : PacketBuilder<IfSetEventsPacket>(
    opcode = 46,
    size = 12
) {
    override fun build(packet: IfSetEventsPacket, buffer: RSByteBuffer) {
        buffer.writeIntMiddleEndian(packet.packedEvents)
        buffer.writeShort(packet.endIndex)
        buffer.writeInt(packet.packedInterface)
        buffer.writeShortLittleEndian(packet.startIndex)
    }
}
