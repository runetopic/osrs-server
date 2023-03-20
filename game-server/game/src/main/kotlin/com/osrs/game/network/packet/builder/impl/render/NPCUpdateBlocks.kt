package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.game.actor.npc.NPC
import com.osrs.game.actor.render.HighDefinitionRenderBlock
import com.osrs.game.world.World
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class NPCUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls(World.MAX_NPCS)
) : UpdateBlocks<NPC>() {
    override fun buildPendingUpdatesBlocks(actor: NPC) {
        if (actor.renderer.hasHighDefinitionUpdate()) {
            highDefinitionUpdates[actor.index] = actor.renderer.highDefinitionRenderBlocks.buildHighDefinitionUpdates()
        }
    }

    override fun clear() {
        highDefinitionUpdates.fill(null)
    }

    private fun Array<HighDefinitionRenderBlock<*>?>.buildHighDefinitionUpdates(): ByteArray {
        // TODO mask for a third byte if > 65535
        val mask = calculateMask(0x2)
        val size = calculateSize(mask)
        return ByteBuffer.allocate(size).also {
            it.writeMask(mask)
            for (block in this) {
                if (block == null) continue
                block.builder.build(it, block.renderType)
            }
        }.array()
    }
}
