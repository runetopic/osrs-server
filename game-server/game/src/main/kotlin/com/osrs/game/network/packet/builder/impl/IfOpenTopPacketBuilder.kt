package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeShortLittleEndianAdd
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.IfOpenTopPacket
import java.nio.ByteBuffer

@Singleton
class IfOpenTopPacketBuilder : PacketBuilder<IfOpenTopPacket>(
    opcode = 48,
    size = 2
) {
    override fun build(packet: IfOpenTopPacket, buffer: ByteBuffer) = buffer.writeShortLittleEndianAdd(packet.interfaceId)
}
