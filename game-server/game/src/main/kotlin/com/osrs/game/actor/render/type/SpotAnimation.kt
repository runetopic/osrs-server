package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType

data class SpotAnimation(
    val id: Int,
    val speed: Int,
    val height: Int,
    val rotation: Int,
) : RenderType {
    constructor(id: Int) : this(id, 0, 0, 0)
    val packed get() = speed and 0xffff or (height shl 16)
}
