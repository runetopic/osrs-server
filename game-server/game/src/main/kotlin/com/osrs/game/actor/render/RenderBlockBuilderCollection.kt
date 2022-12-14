package com.osrs.game.actor.render

import com.osrs.game.network.packet.builder.impl.sync.block.RenderBlockBuilder

class RenderBlockBuilderCollection(
    private val updates: MutableList<RenderBlockBuilder<*>> = arrayListOf()
) : Collection<RenderBlockBuilder<*>> by updates {

    operator fun plusAssign(render: RenderBlockBuilder<*>) {
        updates.add(render.index, render)
    }

    fun clear() = updates.clear()
}
