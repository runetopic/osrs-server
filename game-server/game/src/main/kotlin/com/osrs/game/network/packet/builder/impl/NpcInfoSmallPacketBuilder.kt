package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.game.actor.player.Viewport
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
        buffer.sync(packet.viewport)
    }

    private fun ByteBuffer.sync(
        viewport: Viewport,
        bits: BitAccess = BitAccess(this)
    ) {
        val size = viewport.npcs.fold(0) { size, npc -> if (npc == null) size else size + 1 }
        bits.writeBits(8, size)
    }
}
