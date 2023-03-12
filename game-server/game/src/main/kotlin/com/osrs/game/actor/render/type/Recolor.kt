package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType

data class Recolor(
    val hue: Int,
    val saturation: Int,
    val luminance: Int,
    val amount: Int,
    val startDelay: Int,
    val endDelay: Int
) : RenderType
