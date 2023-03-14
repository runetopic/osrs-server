package com.osrs.game.network.packet.builder.impl.render

import com.google.inject.Singleton
import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeBytes
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

    fun buildPendingUpdates(other: Player) {
        if (other.renderer.hasHighDefinitionUpdate()) {
            this.highDefinitionUpdates[other.index] = other.renderer.highDefinitionRenderBlocks.buildHighDefinitionUpdates(other)
        }
        this.lowDefinitionUpdates[other.index] = other.renderer.lowDefinitionRenderBlocks.buildLowDefinitionUpdates()
    }

    private fun Array<HighDefinitionRenderBlock<*>?>.buildHighDefinitionUpdates(actor: Player): ByteArray {
        val mask = calculateMask()
        val size = fold(0) { total, block ->
            if (block == null) total + 0 else total + block.builder.size(block.renderType)
        }.let { if (mask > 0xFF) it + 2 else it + 1 }

        return ByteBuffer.allocate(size).apply {
            writeMask(if (mask > 0xff) mask or BLOCK_VALUE else mask)
            for (block in this@buildHighDefinitionUpdates) {
                if (block == null) continue
                val start = position()
                block.builder.build(this, block.renderType)
                actor.renderer.setLowDefinitionRenderingBlock(block, array().sliceArray(start until position()))
            }
        }.array()
    }

    private fun Array<LowDefinitionRenderBlock<*>?>.buildLowDefinitionUpdates(): ByteArray {
        val mask = calculateMask()
        val size = fold(0) { total, block ->
            if (block == null) total + 0 else total + block.bytes.size
        }.let { if (mask > 0xFF) it + 2 else it + 1 }

        return ByteBuffer.allocate(size).apply {
            writeMask(if (mask > 0xff) mask or BLOCK_VALUE else mask)
            for (block in this@buildLowDefinitionUpdates) {
                if (block == null) continue
                writeBytes(block.bytes)
            }
        }.array()
    }

    private fun Array<out RenderBlock<*>?>.calculateMask(): Int {
        var mask = 0

        for (block in this) {
            if (block == null) continue
            mask = mask or block.builder.mask
        }
        return mask
    }

    private fun ByteBuffer.writeMask(mask: Int) {
        if (mask > 0xff) writeShortLittleEndian(mask) else writeByte(mask)
    }

    private companion object {
        const val BLOCK_VALUE = 0x40
    }
}
