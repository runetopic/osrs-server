package com.osrs.game.actor

import com.osrs.common.map.location.Location
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.render.Renderer

abstract class Actor {
    abstract var walkDirection: Direction?
    abstract var runDirection: Direction?
    abstract val renderer: Renderer<*>
    abstract var location: Location
    var lastLocation: Location? = null
    var index = 0

    fun reset() {
        renderer.clearUpdates()
        walkDirection = null
        lastLocation = location
    }
}
