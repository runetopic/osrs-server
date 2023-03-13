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
    size = 14,
) {
    override fun build(packet: ObjAddPacket, buffer: ByteBuffer) {
        buffer.writeByte(packet.packedOffset)
        buffer.writeByteAdd(packet.disabledOptions.fold(0x1F) { options, option -> options and (1 shl option).inv() }) // Clear bit at position 'option' to disable that option
        buffer.writeByteAdd(0) // Unused
        buffer.writeShortLittleEndian(0) // Unused
        buffer.writeShortAdd(packet.id)
        buffer.writeInt(packet.quantity)
        buffer.writeByteAdd(0) // Unused
        buffer.writeShortAdd(0) // Unused
    }
}
