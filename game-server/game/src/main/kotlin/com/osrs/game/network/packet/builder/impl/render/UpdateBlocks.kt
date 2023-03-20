package com.osrs.game.network.packet.builder.impl.render

import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeShortLittleEndian
import com.osrs.game.actor.Actor
import com.osrs.game.actor.render.HighDefinitionRenderBlock
import com.osrs.game.actor.render.LowDefinitionRenderBlock
import com.osrs.game.actor.render.RenderBlock
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
abstract class UpdateBlocks<A : Actor> {
    abstract fun buildPendingUpdatesBlocks(actor: A)

    abstract fun clear()

    fun Array<out RenderBlock<*>?>.calculateMask(comparator: Int): Int = fold(0) { mask, block ->
        if (block == null) mask else mask or block.builder.mask
    }.let { if (it > 0xFF) it or comparator else it }

    fun Array<out RenderBlock<*>?>.calculateSize(mask: Int): Int = fold(0) { size, block ->
        if (block == null) return@fold size
        when (block) {
            is LowDefinitionRenderBlock -> size + block.bytes.size
            is HighDefinitionRenderBlock -> size + block.builder.size(block.renderType)
            else -> throw AssertionError("Block is not in correct instance.")
        }
    }.let { if (mask > 0xFF) it + 2 else it + 1 }

    fun ByteBuffer.writeMask(mask: Int) {
        if (mask > 0xff) writeShortLittleEndian(mask) else writeByte(mask)
    }
}
