package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.HighDefinitionRenderBlock
import com.osrs.game.actor.render.LowDefinitionRenderBlock
import com.osrs.game.world.World
import java.nio.ByteBuffer

@Singleton
class PlayerUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS),
    val lowDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS)
) : UpdateBlocks<Player>() {
    override fun buildPendingUpdatesBlocks(actor: Player) {
        if (actor.renderer.hasHighDefinitionUpdate()) {
            highDefinitionUpdates[actor.index] = actor.renderer.highDefinitionRenderBlocks.buildHighDefinitionUpdates(actor)
        }
        // Low definitions are always built here for persisted blocks from previous game cycles. i.e Appearance.
        lowDefinitionUpdates[actor.index] = actor.renderer.lowDefinitionRenderBlocks.buildLowDefinitionUpdates()
    }

    override fun clear() {
        lowDefinitionUpdates.fill(null)
        highDefinitionUpdates.fill(null)
    }

    private fun Array<HighDefinitionRenderBlock<*>?>.buildHighDefinitionUpdates(player: Player): ByteArray {
        val mask = calculateMask(0x40)
        val size = calculateSize(mask)
        return RSByteBuffer(ByteBuffer.allocate(size)).also {
            it.writeMask(mask)
            for (block in this) {
                if (block == null) continue
                val start = it.position()
                block.builder.build(it, block.renderType)
                val end = it.position()
                player.renderer.setLowDefinitionRenderingBlock(block, it.array().sliceArray(start until end))
            }
        }.array()
    }

    private fun Array<LowDefinitionRenderBlock<*>?>.buildLowDefinitionUpdates(): ByteArray {
        val mask = calculateMask(0x40)
        val size = calculateSize(mask)
        return RSByteBuffer(ByteBuffer.allocate(size)).also {
            it.writeMask(mask)
            for (block in this) {
                if (block == null) continue
                it.writeBytes(block.bytes)
            }
        }.array()
    }
}
