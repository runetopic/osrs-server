package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeIntLittleEndian
import com.osrs.common.buffer.writeShortAdd
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.VarpLargePacket
import java.nio.ByteBuffer

@Singleton
class VarpLargePacketBuilder : PacketBuilder<VarpLargePacket>(
    opcode = 5,
    size = 6
) {
    override fun build(packet: VarpLargePacket, buffer: ByteBuffer) {
        buffer.writeShortAdd(packet.id)
        buffer.writeIntLittleEndian(packet.value)
    }
}
