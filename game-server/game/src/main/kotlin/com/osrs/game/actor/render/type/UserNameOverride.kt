package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType

data class UserNameOverride(
    val prefix: String,
    val infix: String,
    val suffix: String
) : RenderType
