package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket

@Singleton
class UpdateZoneFullFollowsPacketBuilder : PacketBuilder<UpdateZoneFullFollowsPacket>(
    opcode = 12,
    size = 2
) {
    override fun build(packet: UpdateZoneFullFollowsPacket, buffer: RSByteBuffer) {
        buffer.writeByteAdd(packet.xInScene)
        buffer.writeByte(packet.zInScene)
    }
}
