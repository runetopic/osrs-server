package com.osrs.game.actor.hit

import com.osrs.game.actor.Actor

data class HitSplat(
    val source: Actor?,
    val type: HitType,
    val amount: Int,
    val delay: Int
)
