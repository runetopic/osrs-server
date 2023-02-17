package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeInt
import com.osrs.common.buffer.writeShortAdd
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.ObjAddPacket
import java.nio.ByteBuffer

@Singleton
class ObjAddPacketBuilder : PacketBuilder<ObjAddPacket>(
    opcode = 31,
    size = 14
) {
    override fun build(packet: ObjAddPacket, buffer: ByteBuffer) {
        buffer.writeByte(packet.packedOffset)
        buffer.writeByteAdd(0x0) // Unsure to disable options 0x0 to disable all 5 0x1 to enable options.
        buffer.writeByteAdd(0) // Unused
        buffer.writeShortLittleEndian(0) // Unused
        buffer.writeShortAdd(packet.id)
        buffer.writeInt(packet.amount)
        buffer.writeByteAdd(0) // Unused
        buffer.writeShortAdd(0) // Unused
    }
}
