package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.BitAccess
import com.osrs.common.buffer.withBitAccess
import com.osrs.common.map.location.ZoneLocation
import com.osrs.common.map.location.withinDistance
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.player.Viewport
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.NpcInfoPacket
import com.osrs.game.world.map.zone.Zone
import com.osrs.game.world.map.zone.ZoneManager
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class NpcInfoSmallPacketBuilder : PacketBuilder<NpcInfoPacket>(
    opcode = 53,
    size = -2
) {
    override fun build(packet: NpcInfoPacket, buffer: ByteBuffer) {
        buffer.sync(packet.viewport, packet.highDefinitionUpdates)
    }

    private fun ByteBuffer.sync(
        viewport: Viewport,
        updates: Array<ByteArray?>,
        bits: BitAccess = BitAccess(this)
    ) {
        val size = viewport.npcs.fold(0) { size, npc -> if (npc == null) size else size + 1 }
        bits.writeBits(8, size)
        bits.syncHighDefinition(viewport)
        bits.syncLowDefinition(viewport, updates)
        withBitAccess(bits)
    }

    private fun BitAccess.syncHighDefinition(
        viewport: Viewport
    ) {
        for (npc in viewport.npcs) {
            if (npc == null) continue
            writeBit(false)
        }
    }

    private fun BitAccess.syncLowDefinition(
        viewport: Viewport,
        updates: Array<ByteArray?>
    ) {
        val player = viewport.player
        val npcs = player.zones.map { ZoneManager[ZoneLocation(it)] }.map(Zone::npcs).flatten()
        for (npc in npcs) {
            val adding = viewport.shouldAdd(npc)
            if (!adding) continue
            processLowDefinitionNpc(viewport, npc)
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
