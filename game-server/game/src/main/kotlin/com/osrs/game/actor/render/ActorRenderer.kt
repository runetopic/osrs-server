package com.osrs.game.actor.render

class ActorRenderer(
    val lowDefinitionRenderBlocks: Array<RenderBlock<*>?> = arrayOfNulls(13),
    val highDefinitionRenderBlocks: Array<RenderBlock<*>?> = arrayOfNulls(13)
) {
    fun <T : RenderType> update(type: T): T {
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = RenderBlock(type, block)
        return type
    }

    fun hasRenderBlockUpdate(): Boolean = highDefinitionRenderBlocks.any { it != null }

    fun clearRenderBlocks() {
        // Clear out the pending render blocks.
        highDefinitionRenderBlocks.fill(null)
    }
}
