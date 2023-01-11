package com.osrs.game.network.packet.server.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.network.packet.server.IfOpenTopPacket
import com.osrs.game.network.packet.server.builder.PacketBuilder
import java.nio.ByteBuffer

@Singleton
class IfOpenTopPacketBuilder : PacketBuilder<IfOpenTopPacket>(
    opcode = 25,
    size = 2
) {
    override fun build(packet: IfOpenTopPacket, buffer: ByteBuffer) = buffer.writeShortLittleEndian(packet.interfaceId)
}
