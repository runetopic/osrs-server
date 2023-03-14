package com.osrs.game.actor.hit

import com.osrs.game.actor.Actor

@JvmInline
value class HealthBar(val id: Int) {
    companion object {
        val Default = HealthBar(0)
        val Medium = HealthBar(10)
        val Large = HealthBar(20)
    }

    fun percentage(e: Actor): Int {
        val total = e.totalHitpoints()
        val current = e.currentHitpoints().coerceAtMost(total)
        // TODO 30 is only hard-coded for players
        var percentage = if (total == 0) 0 else current * 30 / total
        if (percentage == 0 && current > 0) {
            percentage = 1
        }
        return percentage
    }
}
