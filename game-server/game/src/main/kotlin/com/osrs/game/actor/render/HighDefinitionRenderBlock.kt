package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder

data class HighDefinitionRenderBlock<T : Actor, R : RenderType>(
    val renderType: RenderType,
    override val builder: RenderBlockBuilder<T, R>
) : RenderBlock<T, R>(builder)
