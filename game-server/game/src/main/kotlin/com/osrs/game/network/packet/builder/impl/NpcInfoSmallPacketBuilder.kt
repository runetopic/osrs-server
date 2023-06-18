package com.osrs.game.network.packet.builder.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.api.map.location.withinDistance
import com.osrs.game.actor.movement.MoveDirection
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

            val shouldRemove = !npc.location.withinDistance(viewport.player.location)

            if (shouldRemove) {
                writeBits(1, 1)
                writeBits(2, 3)
                iterator.remove()
                continue
            }

            val hasBlockUpdate = false

            val isMoving = npc.moveDirection != MoveDirection.None

            val hasUpdate = hasBlockUpdate || isMoving

            writeBits(1, if (hasUpdate) 1 else 0)

            if (isMoving) {
                writeBits(2, 1)
                writeBits(3, npc.moveDirection.walkDirection?.npcOpcode ?: 0)
                writeBits(1, if (hasBlockUpdate) 1 else 0)
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
        if (npc.id != 3216) {
            return
        }
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
