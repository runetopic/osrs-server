package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
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
        highDefinitionRenderBlocks.fill(null)

        for (lowDefBlock in lowDefinitionRenderBlocks) {
            if (lowDefBlock == null) continue

            val renderType = lowDefBlock.renderType
            // Persist these render types.
            if (renderType is Appearance || renderType is MovementSpeed || renderType is FaceAngle) continue

            val index = lowDefinitionRenderBlocks.indexOf(lowDefBlock)
            if (index == -1) continue
            lowDefinitionRenderBlocks[index] = null
        }
    }
}
