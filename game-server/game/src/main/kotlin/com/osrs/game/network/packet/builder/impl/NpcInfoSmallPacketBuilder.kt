package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.type.server.NpcInfoPacket
import com.osrs.game.world.map.zone.ZoneManager
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class NpcInfoSmallPacketBuilder @Inject constructor(
    private val updateBlocks: NPCUpdateBlocks
) : PacketBuilder<NpcInfoPacket>(
    opcode = 53,
    size = -2
) {
    override fun build(packet: NpcInfoPacket, buffer: ByteBuffer) {
        buffer.sync(packet.viewport)
    }

    private fun ByteBuffer.sync(
        viewport: Viewport,
        bits: BitAccess = BitAccess(this)
    ) {
        bits.writeBits(8, viewport.npcs.size)
        bits.syncHighDefinition(viewport)
        bits.syncLowDefinition(viewport)
        withBitAccess(bits)
    }

    private fun BitAccess.syncHighDefinition(
        viewport: Viewport
    ) {
        for (npc in viewport.npcs) {
            val updating = false
            if (!updating) {
                writeBit(false)
                continue
            }
            writeBit(true)
            // TODO
        }
    }

    private fun BitAccess.syncLowDefinition(
        viewport: Viewport
    ) {
        val player = viewport.player
        for (zone in player.zones) {
            for (npc in ZoneManager[ZoneLocation(zone)].npcs) {
                val adding = viewport.shouldAdd(npc)
                if (!adding) continue
                processLowDefinitionNpc(viewport, npc)
            }
        }
    }

    private fun BitAccess.processLowDefinitionNpc(
        viewport: Viewport,
        npc: NPC
    ) {
        val player = viewport.player
        writeBits(15, npc.index)
        writeBits(5, (npc.location.z - player.location.z).let { if (it < 15) it + 32 else it })
        writeBits(14, npc.id)
        writeBits(1, 0)
        writeBit(false) // TODO updating
        writeBits(3, 0) // TODO orientation
        writeBits(5, (npc.location.x - player.location.x).let { if (it < 15) it + 32 else it })
        writeBit(false) // TODO handle teleporting
        viewport.npcs += npc
    }

    private fun Viewport.shouldAdd(npc: NPC): Boolean = when {
        npc in npcs -> false
        !npc.location.withinDistance(player.location) -> false
        else -> true
    }
}
