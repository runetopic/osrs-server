package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed

class ActorRenderer {
    private val lowDefinitionRenderBlocks: Array<LowDefinitionRenderBlock<*, *>?> = arrayOfNulls(13)
    private val highDefinitionRenderBlocks: Array<HighDefinitionRenderBlock<*, *>?> = arrayOfNulls(13)

    fun <T : RenderType> update(type: T): T {
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(type, block)
        return type
    }

    fun setLowDefinitionRenderingBlock(highDefinitionRenderingBlock: HighDefinitionRenderBlock<*, *>, bytes: ByteArray) {
        val lowDefinitionRenderingBlock = LowDefinitionRenderBlock(
            renderType = highDefinitionRenderingBlock.renderType,
            builder = highDefinitionRenderingBlock.builder,
            bytes = bytes,
        )
        lowDefinitionRenderBlocks[highDefinitionRenderingBlock.builder.index] = lowDefinitionRenderingBlock
    }

    fun highDefinitionUpdates(): Array<HighDefinitionRenderBlock<*, *>?> = highDefinitionRenderBlocks
    fun lowDefinitionUpdates(): Array<LowDefinitionRenderBlock<*, *>?> = lowDefinitionRenderBlocks
    fun hasHighDefinitionUpdate(): Boolean = highDefinitionRenderBlocks.isNotEmpty()

    fun clearUpdates() {
        highDefinitionRenderBlocks.fill(null)

        for (lowDefBlock in lowDefinitionRenderBlocks) {
            if (lowDefBlock == null) continue

            if (lowDefBlock.renderType !is Appearance && lowDefBlock.renderType !is MovementSpeed) {
                val index = lowDefinitionRenderBlocks.indexOf(lowDefBlock)

                if (index == -1) continue

                lowDefinitionRenderBlocks[index] = null
            }
        }
    }
}
