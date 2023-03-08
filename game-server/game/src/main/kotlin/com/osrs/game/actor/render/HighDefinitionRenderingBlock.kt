package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.builder.impl.sync.RenderingBlock

data class HighDefinitionRenderingBlock<T : Actor, R : RenderType>(
    val renderType: RenderType,
    override val block: RenderingBlock<T, R>
) : AbstractRenderingBlock<T, R>(block)

abstract class AbstractRenderingBlock<T : Actor, R : RenderType>(open val block: RenderingBlock<T, R>)
