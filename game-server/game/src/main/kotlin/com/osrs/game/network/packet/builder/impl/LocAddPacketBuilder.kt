package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.LocAddPacket

@Singleton
class LocAddPacketBuilder : PacketBuilder<LocAddPacket>(
    opcode = 23,
    size = 5
) {
    override fun build(packet: LocAddPacket, buffer: RSByteBuffer) {
        buffer.writeByteAdd(packet.disabledOptions.fold(0x1F) { options, option -> options and (1 shl option).inv() }) // Clear bit at position 'option' to disable that option
        buffer.writeShortLittleEndian(packet.id)
        buffer.writeByteNegate(packet.packedOffset)
        buffer.writeByte((packet.shape shl 2) or (packet.rotation and 0x3))
    }
}
