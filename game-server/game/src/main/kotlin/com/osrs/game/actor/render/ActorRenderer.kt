package com.osrs.game.actor.render

import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.actor.render.type.MovementSpeed
import com.osrs.game.actor.render.type.MovementSpeedType
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.actor.render.type.TemporaryMovementSpeed
import com.osrs.game.actor.render.type.UserNameOverride

class ActorRenderer {
    private val lowDefinitionRenderBlocks: Array<LowDefinitionRenderBlock<*, *>?> = arrayOfNulls(13)
    private val highDefinitionRenderBlocks: Array<HighDefinitionRenderBlock<*, *>?> = arrayOfNulls(13)

    fun recolor(recolor: Recolor) {
        val block = recolor.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(recolor, block)
    }

    fun userNameOverride(override: UserNameOverride) {
        val block = override.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(override, block)
    }

    fun appearance(appearance: Appearance): Appearance {
        val block = appearance.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(appearance, block)
        return appearance
    }

    fun updateMovementSpeed(speed: MovementSpeedType) {
        val type = MovementSpeed(speed)
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(type, block)
    }

    fun temporaryMovementSpeed(speed: MovementSpeedType) {
        val type = TemporaryMovementSpeed(speed)
        val block = type.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(type, block)
    }

    fun publicChat(publicChat: PublicChat) {
        val block = publicChat.toBlock()
        highDefinitionRenderBlocks[block.index] = HighDefinitionRenderBlock(publicChat, block)
    }

    fun setLowDefinitionRenderingBlock(highDefinitionRenderingBlock: HighDefinitionRenderBlock<*, *>, bytes: ByteArray) {
        val lowDefinitionRenderingBlock = LowDefinitionRenderBlock(
            renderType = highDefinitionRenderingBlock.renderType,
            builder = highDefinitionRenderingBlock.builder,
            bytes = bytes
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
