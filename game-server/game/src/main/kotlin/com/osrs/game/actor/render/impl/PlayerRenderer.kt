package com.osrs.game.actor.render.impl

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.actor.render.RenderBlocks.playerRenderingBlocks
import com.osrs.game.actor.render.Renderer
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderBlockBuilder

class PlayerRenderer : Renderer<Player>() {

    fun refreshAppearance(appearance: RenderBlock.Appearance): RenderBlock.Appearance {
        updates += appearance.getRenderBlockBuilder()
        return appearance
    }

    private fun RenderBlock.getRenderBlockBuilder(): RenderBlockBuilder<Player> =
        playerRenderingBlocks[this::class] ?: throw IllegalStateException("Unhandled player render block. ${this::class.qualifiedName}")
}
