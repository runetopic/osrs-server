package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.server.IfOpenTopPacket
import xlitekt.shared.buffer.writeShortLittleEndian
import java.nio.ByteBuffer

@Singleton
class IfOpenTopPacketBuilder : PacketBuilder<IfOpenTopPacket>(
    opcode = 25,
    size = 2
) {
    override fun build(packet: IfOpenTopPacket, buffer: ByteBuffer) = buffer.writeShortLittleEndian(packet.interfaceId)
}
