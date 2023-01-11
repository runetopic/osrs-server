package com.osrs.game.actor.render

import com.osrs.game.actor.Actor
import com.osrs.game.network.packet.server.builder.impl.sync.block.RenderBlockBuilder

class RenderBlockBuilderCollection<T : Actor>(
    private val updates: MutableList<RenderBlockBuilder<T>> = arrayListOf()
) : Collection<RenderBlockBuilder<T>> by updates {

    operator fun plusAssign(render: RenderBlockBuilder<T>) {
        updates.add(render.index, render)
    }

    fun clear() = updates.clear()
}
