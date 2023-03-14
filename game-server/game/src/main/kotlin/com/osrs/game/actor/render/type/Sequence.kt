package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType

data class Sequence(
    val id: Int,
    val delay: Int
) : RenderType {
    constructor(id: Int) : this(id, 0)
}
