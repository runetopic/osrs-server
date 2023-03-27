package com.osrs.game.network.packet.builder.impl.render

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.Actor
import com.osrs.game.actor.render.RenderBlock

/**
 * @author Jordan Abraham
 */
interface UpdateBlocks<A : Actor> {
    fun buildPendingUpdatesBlocks(actor: A)
    fun clear()

    fun Array<out RenderBlock<*>?>.calculateHighDefinitionMask(comparator: Int): Int = fold(0) { mask, block ->
        if (block == null) return@fold mask
        return@fold mask or block.builder.mask
    }.let { if (it > 0xFF) it or comparator else it }

    fun Array<out RenderBlock<*>?>.calculateLowDefinitionMask(comparator: Int): Int = fold(0) { mask, block ->
        if (block == null) return@fold mask
        if (!block.builder.persisted) throw IllegalAccessException("Block is not persisted and should not be calculated here.")
        return@fold mask or block.builder.mask
    }.let { if (it > 0xFF) it or comparator else it }

    fun Array<out RenderBlock<*>?>.calculateHighDefinitionSize(mask: Int): Int = fold(0) { size, block ->
        if (block == null) return@fold size
        return@fold size + block.builder.size(block.renderType)
    }.let { if (mask > 0xFF) it + 2 else it + 1 }

    fun Array<out RenderBlock<*>?>.calculateLowDefinitionSize(mask: Int): Int = fold(0) { size, block ->
        if (block == null) return@fold size
        if (!block.builder.persisted) throw IllegalAccessException("Block is not persisted and should not be calculated here.")
        return@fold size + block.builder.size(block.renderType)
    }.let { if (mask > 0xFF) it + 2 else it + 1 }

    fun RSByteBuffer.writeMask(mask: Int) {
        if (mask > 0xFF) writeShortLittleEndian(mask) else writeByte(mask)
    }
}
