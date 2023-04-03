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
 * @author Tyler Telis
 */
@Singleton
class NpcInfoSmallPacketBuilder @Inject constructor(
    private val updateBlocks: NPCUpdateBlocks
) : PacketBuilder<NpcInfoPacket>(
    opcode = 53,
    size = -2
) {
    override fun build(packet: NpcInfoPacket, buffer: RSByteBuffer) {
        buffer.accessBits()
        buffer.sync(packet.viewport)
        buffer.accessBytes()
    }

    private fun RSByteBuffer.sync(
        viewport: Viewport
    ) {
        val size = viewport.npcs.size
        writeBits(8, size)
        syncHighDefinition(viewport)
        syncLowDefinition(viewport)
    }

    private fun RSByteBuffer.syncHighDefinition(
        viewport: Viewport
    ) {
        val iterator = viewport.npcs.iterator()

        while (iterator.hasNext()) {
            val npc = iterator.next()
            val hasBlockUpdate = false
            val shouldRemove = !npc.location.withinDistance(viewport.player.location)
            val hasUpdate = shouldRemove || hasBlockUpdate

            writeBits(1, if (hasUpdate) 1 else 0)

            if (shouldRemove) {
                writeBits(2, 3)
                iterator.remove()
            }
        }
    }

    private tailrec fun RSByteBuffer.syncLowDefinition(
        viewport: Viewport,
        zoneIndex: Int = 0,
        npcIndex: Int = 0
    ) {
        val player = viewport.player

        if (zoneIndex >= player.zones.size) return

        val zone = player.zones[zoneIndex]

        if (npcIndex >= zone.npcs.size) return syncLowDefinition(viewport, zoneIndex + 1, 0)

        val npc = zone.npcs.elementAt(npcIndex)

        val adding = viewport.shouldAdd(npc)

        if (!adding) return syncLowDefinition(viewport, zoneIndex, npcIndex + 1)

        processLowDefinitionNpc(viewport, npc)

        return syncLowDefinition(viewport, zoneIndex, npcIndex + 1)
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

    private fun Viewport.shouldAdd(npc: NPC): Boolean = npc !in npcs && npc.location.withinDistance(player.location)
}
