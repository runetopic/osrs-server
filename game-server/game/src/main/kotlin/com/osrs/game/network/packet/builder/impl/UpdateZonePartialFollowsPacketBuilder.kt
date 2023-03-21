package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket

@Singleton
class UpdateZonePartialFollowsPacketBuilder : PacketBuilder<UpdateZonePartialFollowsPacket>(
    opcode = 13,
    size = 2
) {
    override fun build(packet: UpdateZonePartialFollowsPacket, buffer: RSByteBuffer) {
        buffer.writeByteNegate(packet.zInScene)
        buffer.writeByte(packet.xInScene)
    }
}
