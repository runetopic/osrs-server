package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.server.PlayerInfoPacket
import xlitekt.shared.buffer.writeBytes
import java.nio.ByteBuffer

@Singleton
class PlayerInfoPacketBuilder : PacketBuilder<PlayerInfoPacket>(
    opcode = 77,
    size = -2
) {
    override fun build(packet: PlayerInfoPacket, buffer: ByteBuffer) = buffer.writeBytes(packet.buffer)
}
