package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.NpcInfoPacket
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class NpcInfoSmallPacketBuilder : PacketBuilder<NpcInfoPacket>(
    opcode = 9,
    size = -2
) {
    override fun build(packet: NpcInfoPacket, buffer: ByteBuffer) {
        TODO("Not yet implemented")
    }
}
