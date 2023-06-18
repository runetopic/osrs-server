package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.VarpLargePacket

@Singleton
class VarpLargePacketBuilder : PacketBuilder<VarpLargePacket>(
    opcode = 26,
    size = 6
) {
    override fun build(packet: VarpLargePacket, buffer: RSByteBuffer) {
        buffer.writeShort(packet.id)
        buffer.writeIntLittleEndian(packet.value)
    }
}
