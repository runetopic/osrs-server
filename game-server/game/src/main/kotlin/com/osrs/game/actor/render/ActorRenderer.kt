package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.MovementSpeedType
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed

class ActorRenderer {
    private val lowDefinitionRenderBlocks: Array<LowDefinitionRenderingBlock<*, *>?> = arrayOfNulls(13)
    private val highDefinitionRenderBlocks: Array<HighDefinitionRenderingBlock<*, *>?> = arrayOfNulls(13)

    fun appearance(appearance: Appearance): Appearance {
        val block = appearance.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderingBlock(appearance, block)
        return appearance
    }

    fun updateMovementSpeed(speed: MovementSpeedType) {
        val type = MovementSpeed(speed)
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderingBlock(type, block)
    }

    fun temporaryMovementSpeed(speed: MovementSpeedType) {
        val type = TemporaryMovementSpeed(speed)
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderingBlock(type, block)
    }

    fun setLowDefinitionRenderingBlock(highDefinitionRenderingBlock: HighDefinitionRenderingBlock<*, *>, bytes: ByteArray) {
        val lowDefinitionRenderingBlock = LowDefinitionRenderingBlock(
            renderType = highDefinitionRenderingBlock.renderType,
            block = highDefinitionRenderingBlock.block,
            bytes = bytes
        )
        lowDefinitionRenderBlocks[highDefinitionRenderingBlock.block.index] = lowDefinitionRenderingBlock
    }

    fun highDefinitionUpdates(): Array<HighDefinitionRenderingBlock<*, *>?> = highDefinitionRenderBlocks
    fun lowDefinitionUpdates(): Array<LowDefinitionRenderingBlock<*, *>?> = lowDefinitionRenderBlocks
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
