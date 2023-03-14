package com.osrs.game.actor.render.type

import com.osrs.game.actor.Actor
import com.osrs.game.actor.hit.HealthBar
import com.osrs.game.actor.hit.HitSplat
import com.osrs.game.actor.render.RenderType

data class HealthUpdate(
    val source: Actor,
    val splats: List<HitSplat>,
    val bars: List<HealthBar>
) : RenderType
