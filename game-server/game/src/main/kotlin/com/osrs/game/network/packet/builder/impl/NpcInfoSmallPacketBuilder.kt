package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.type.server.NpcInfoPacket
import com.osrs.game.world.map.zone.ZoneManager.npcs

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
    override fun build(packet: NpcInfoPacket, buffer: RSByteBuffer) {
        buffer.bitAccess { buffer.sync(packet.viewport) }
    }

    private fun RSByteBuffer.sync(
        viewport: Viewport
    ) {
        writeBits(8, viewport.npcs.size)
        syncHighDefinition(viewport)
        syncLowDefinition(viewport)
    }

    private fun RSByteBuffer.syncHighDefinition(
        viewport: Viewport
    ) {
        for (npc in viewport.npcs) {
            val updating = false
            if (!updating) {
                writeBits(1, 0)
                continue
            }
            writeBits(1, 1)
            // TODO
        }
    }

    private fun RSByteBuffer.syncLowDefinition(
        viewport: Viewport
    ) {
        val player = viewport.player
        for (zone in player.zones) {
            for (npc in zone.npcs) {
                val adding = viewport.shouldAdd(npc)
                if (!adding) continue
                processLowDefinitionNpc(viewport, npc)
            }
        }
    }

    private fun RSByteBuffer.processLowDefinitionNpc(
        viewport: Viewport,
        npc: NPC
    ) {
        val player = viewport.player
        writeBits(15, npc.index)
        writeBits(5, (npc.location.z - player.location.z).let { if (it < 15) it + 32 else it })
        writeBits(14, npc.id)
        writeBits(1, 0)
        writeBits(1, 0) // TODO updating
        writeBits(3, 0) // TODO orientation
        writeBits(5, (npc.location.x - player.location.x).let { if (it < 15) it + 32 else it })
        writeBits(1, 0) // TODO handle teleporting
        viewport.npcs += npc
    }

    private fun Viewport.shouldAdd(npc: NPC): Boolean = when {
        npc in npcs -> false
        !npc.location.withinDistance(player.location) -> false
        else -> true
    }
}
