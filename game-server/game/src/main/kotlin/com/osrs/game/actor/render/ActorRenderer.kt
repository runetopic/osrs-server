package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.FaceActor
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.actor.render.type.MovementSpeed

class ActorRenderer(
    val lowDefinitionRenderBlocks: Array<LowDefinitionRenderBlock<*>?> = arrayOfNulls(13),
    val highDefinitionRenderBlocks: Array<HighDefinitionRenderBlock<*>?> = arrayOfNulls(13)
) {
    fun <T : RenderType> update(type: T): T {
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(type, block)
        return type
    }

    fun setLowDefinitionRenderingBlock(highDefinitionRenderingBlock: HighDefinitionRenderBlock<*>, bytes: ByteArray) {
        val lowDefinitionRenderingBlock = LowDefinitionRenderBlock(
            renderType = highDefinitionRenderingBlock.renderType,
            builder = highDefinitionRenderingBlock.builder,
            bytes = bytes
        )
        lowDefinitionRenderBlocks[highDefinitionRenderingBlock.builder.index] = lowDefinitionRenderingBlock
    }

    fun hasHighDefinitionUpdate(): Boolean = highDefinitionRenderBlocks.any { it != null }

    fun clearUpdates() {
        // Clear out the pending high definition blocks.
        highDefinitionRenderBlocks.fill(null)
        // Clear out the pending low definition blocks.
        for (index in lowDefinitionRenderBlocks.indices) {
            val renderType = lowDefinitionRenderBlocks[index]?.renderType ?: continue
            // Persist these render types.
            if (renderType is Appearance || renderType is MovementSpeed || renderType is FaceAngle || renderType is FaceActor) continue
            lowDefinitionRenderBlocks[index] = null
        }
    }
}
