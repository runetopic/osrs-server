package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.npc.NPC
import com.osrs.game.world.World
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class NPCUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls(World.MAX_NPCS)
) : UpdateBlocks<NPC> {
    override fun buildPendingUpdatesBlocks(actor: NPC) {
        val renderer = actor.renderer
        if (!renderer.hasRenderBlockUpdate()) return

        val highDefBlocks = renderer.highDefinitionRenderBlocks
        val mask = highDefBlocks.calculateHighDefinitionMask(0x2) // TODO mask for a third byte if > 65535
        val size = highDefBlocks.calculateHighDefinitionSize(mask)
        val buffer = RSByteBuffer(ByteBuffer.allocate(size)).also {
            it.writeMask(mask)
            for (block in highDefBlocks) {
                if (block == null) continue
                block.builder.build(it, block.renderType)
            }
        }

        highDefinitionUpdates[actor.index] = buffer.array()
    }

    override fun clear() {
        highDefinitionUpdates.fill(null)
    }
}
