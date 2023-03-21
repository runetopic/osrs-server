package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfOpenTopPacket

@Singleton
class IfOpenTopPacketBuilder : PacketBuilder<IfOpenTopPacket>(
    opcode = 48,
    size = 2
) {
    override fun build(packet: IfOpenTopPacket, buffer: RSByteBuffer) {
        buffer.writeShortLittleEndianAdd(packet.interfaceId)
    }
}
