package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.ObjRemovePacket

@Singleton
class ObjRemovePacketBuilder : PacketBuilder<ObjRemovePacket>(
    opcode = 24,
    size = 7
) {
    override fun build(packet: ObjRemovePacket, buffer: RSByteBuffer) {
        buffer.writeByteAdd(packet.packedOffset)
        buffer.writeShortLittleEndian(packet.id)
        buffer.writeIntMiddleEndian(packet.quantity)
    }
}
