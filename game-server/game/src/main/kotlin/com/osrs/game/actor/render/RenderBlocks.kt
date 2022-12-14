package com.osrs.game.actor.render

import com.osrs.game.network.packet.builder.impl.sync.block.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.sync.block.player.PlayerAppearanceBlock
import kotlin.reflect.KClass

object RenderBlocks {
    val playerRenderingBlocks = mapOf<KClass<*>, RenderBlockBuilder<*>>(
        RenderBlock.Appearance::class to PlayerAppearanceBlock()
    )
}
