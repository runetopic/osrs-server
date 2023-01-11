package com.osrs.game.network.packet.server.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.writeBytes
import com.osrs.game.network.packet.server.PlayerInfoPacket
import com.osrs.game.network.packet.server.builder.PacketBuilder
import java.nio.ByteBuffer

@Singleton
class PlayerInfoPacketBuilder : PacketBuilder<PlayerInfoPacket>(
    opcode = 77,
    size = -2
) {
    override fun build(packet: PlayerInfoPacket, buffer: ByteBuffer) = buffer.writeBytes(packet.buffer)
}
