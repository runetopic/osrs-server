package com.osrs.game.actor.render.type

import com.osrs.game.actor.Actor
import com.osrs.game.actor.hit.HealthBar
import com.osrs.game.actor.hit.HitSplat
import com.osrs.game.actor.render.RenderType

data class HealthUpdate(
    val source: Actor,
    val splats: Array<HitSplat>,
    val bars: Array<HealthBar>
) : RenderType {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HealthUpdate

        if (source != other.source) return false
        if (!splats.contentEquals(other.splats)) return false
        if (!bars.contentEquals(other.bars)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + splats.contentHashCode()
        result = 31 * result + bars.contentHashCode()
        return result
    }
}
