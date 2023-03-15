package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.HighDefinitionRenderBlock
import com.osrs.game.actor.render.LowDefinitionRenderBlock
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.world.World
import java.nio.ByteBuffer

@Singleton
class PlayerUpdateBlocks(
    val highDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS),
    val lowDefinitionUpdates: Array<ByteArray?> = arrayOfNulls<ByteArray?>(World.MAX_PLAYERS)
) {
    fun clear() {
        lowDefinitionUpdates.fill(null)
        highDefinitionUpdates.fill(null)
    }

    fun buildPendingUpdates(player: Player) {
        if (player.renderer.hasHighDefinitionUpdate()) {
            this.highDefinitionUpdates[player.index] = player.renderer.highDefinitionRenderBlocks.buildHighDefinitionUpdates(player)
        }
        // Low definitions are always built here for persisted blocks from previous game cycles. i.e Appearance.
        this.lowDefinitionUpdates[player.index] = player.renderer.lowDefinitionRenderBlocks.buildLowDefinitionUpdates()
    }

    private fun Array<HighDefinitionRenderBlock<*>?>.buildHighDefinitionUpdates(player: Player): ByteArray {
        val mask = calculateMask()
        val size = calculateSize(mask)
        return ByteBuffer.allocate(size).also {
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
        val mask = calculateMask()
        val size = calculateSize(mask)
        return ByteBuffer.allocate(size).also {
            it.writeMask(mask)
            for (block in this) {
                if (block == null) continue
                it.put(block.bytes)
            }
        }.array()
    }

    private fun Array<out RenderBlock<*>?>.calculateMask(): Int = fold(0) { mask, block ->
        if (block == null) mask else mask or block.builder.mask
    }.let { if (it > 0xFF) it or BLOCK_VALUE else it }

    private fun Array<out RenderBlock<*>?>.calculateSize(mask: Int): Int = fold(0) { size, block ->
        if (block == null) return@fold size
        when (block) {
            is LowDefinitionRenderBlock -> size + block.bytes.size
            is HighDefinitionRenderBlock -> size + block.builder.size(block.renderType)
            else -> throw AssertionError("Block is not in correct instance.")
        }
    }.let { if (mask > 0xFF) it + 2 else it + 1 }

    private fun ByteBuffer.writeMask(mask: Int) {
        if (mask > 0xff) writeShortLittleEndian(mask) else writeByte(mask)
    }

    private companion object {
        const val BLOCK_VALUE = 0x40
    }
}
