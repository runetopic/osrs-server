package com.osrs.game.actor.render.impl

import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.actor.render.RenderBlocks.playerRenderingBlocks
import com.osrs.game.actor.render.Renderer
import com.osrs.game.network.packet.builder.impl.sync.block.RenderBlockBuilder

class PlayerRenderer : Renderer() {

    fun refreshAppearance(appearance: RenderBlock.Appearance): RenderBlock.Appearance {
        updates += appearance.toRenderBlock()
        return appearance
    }

    private fun RenderBlock.toRenderBlock(): RenderBlockBuilder<*> {
        return playerRenderingBlocks[this::class] ?: throw IllegalStateException("Unhandled player render block. ${this::class.qualifiedName}")
    }
}
