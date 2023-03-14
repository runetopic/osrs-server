package com.osrs.game.actor.render

import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

data class HighDefinitionRenderBlock<R : RenderType>(
    val renderType: RenderType,
    override val builder: RenderBlockBuilder<R>
) : RenderBlock<R>(builder)
