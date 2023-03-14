package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteNegate
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateZonePartialFollowsPacket
import java.nio.ByteBuffer

@Singleton
class UpdateZonePartialFollowsPacketBuilder : PacketBuilder<UpdateZonePartialFollowsPacket>(
    opcode = 13,
    size = 2,
) {
    override fun build(packet: UpdateZonePartialFollowsPacket, buffer: ByteBuffer) {
        buffer.writeByteNegate(packet.zInScene)
        buffer.writeByte(packet.xInScene)
    }
}
