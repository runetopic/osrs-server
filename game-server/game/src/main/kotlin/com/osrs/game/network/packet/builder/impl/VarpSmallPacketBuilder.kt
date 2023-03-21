package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.VarpSmallPacket

@Singleton
class VarpSmallPacketBuilder : PacketBuilder<VarpSmallPacket>(
    opcode = 97,
    size = 3
) {
    override fun build(packet: VarpSmallPacket, buffer: RSByteBuffer) {
        buffer.writeShortAdd(packet.id)
        buffer.writeByteAdd(packet.value)
    }
}
