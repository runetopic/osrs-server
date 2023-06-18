package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.LocRemovePacket

@Singleton
class LocRemovePacketBuilder : PacketBuilder<LocRemovePacket>(
    opcode = 1,
    size = 2
) {
    override fun build(packet: LocRemovePacket, buffer: RSByteBuffer) {
        buffer.writeByte(packet.packedOffset)
        buffer.writeByteSubtract(packet.shape)
    }
}
