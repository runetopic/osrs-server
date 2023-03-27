package com.osrs.game.actor.render

import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

data class RenderBlock<R : RenderType>(
    val renderType: RenderType,
    val builder: RenderBlockBuilder<R>
)
