package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.MovementSpeedType
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed

class ActorRenderer {
    private val highDefinitionRenderBlocks: MutableMap<Int, HighDefinitionRenderingBlock<*, *>> = HashMap()
    private val lowDefinitionRenderBlocks: MutableMap<Int, LowDefinitionRenderingBlock<*, *>> = HashMap()

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

    fun highDefinitionUpdates(): Collection<HighDefinitionRenderingBlock<*, *>> = highDefinitionRenderBlocks.values
    fun lowDefinitionUpdates(): Collection<LowDefinitionRenderingBlock<*, *>> = lowDefinitionRenderBlocks.values
    fun hasHighDefinitionUpdate(): Boolean = highDefinitionRenderBlocks.isNotEmpty()

    fun clearUpdates() {
        highDefinitionRenderBlocks.clear()
        lowDefinitionRenderBlocks.values.removeIf {
            it.renderType !is Appearance && it.renderType !is MovementSpeed // TODO FaceAngle
        }
    }
}
