package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteSubtract
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.LocRemovePacket
import java.nio.ByteBuffer

@Singleton
class LocRemovePacketBuilder : PacketBuilder<LocRemovePacket>(
    opcode = 1,
    size = 2
) {
    override fun build(packet: LocRemovePacket, buffer: ByteBuffer) {
        buffer.writeByte(packet.packedOffset)
        buffer.writeByteSubtract(packet.shape)
    }
}
