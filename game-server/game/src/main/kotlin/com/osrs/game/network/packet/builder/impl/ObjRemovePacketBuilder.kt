package com.osrs.game.network.packet.builder.impl

import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeIntV1
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.ObjRemovePacket
import java.nio.ByteBuffer
import javax.inject.Singleton

@Singleton
class ObjRemovePacketBuilder : PacketBuilder<ObjRemovePacket>(
    opcode = 24,
    size = 7,
) {
    override fun build(packet: ObjRemovePacket, buffer: ByteBuffer) {
        buffer.writeByteAdd(packet.packedOffset)
        buffer.writeShortLittleEndian(packet.id)
        buffer.writeIntV1(packet.quantity)
    }
}
