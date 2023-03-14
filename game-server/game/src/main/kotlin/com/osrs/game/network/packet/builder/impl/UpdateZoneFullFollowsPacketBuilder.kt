package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteAdd
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.UpdateZoneFullFollowsPacket
import java.nio.ByteBuffer

@Singleton
class UpdateZoneFullFollowsPacketBuilder : PacketBuilder<UpdateZoneFullFollowsPacket>(
    opcode = 12,
    size = 2
) {
    override fun build(packet: UpdateZoneFullFollowsPacket, buffer: ByteBuffer) {
        buffer.writeByteAdd(packet.xInScene)
        buffer.writeByte(packet.zInScene)
    }
}
