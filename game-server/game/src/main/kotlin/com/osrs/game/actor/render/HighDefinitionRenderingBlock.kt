package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.builder.impl.sync.RenderingBlock

data class HighDefinitionRenderingBlock<T : Actor, R : RenderType>(
    val renderType: RenderType,
    val block: RenderingBlock<T, R>
)
