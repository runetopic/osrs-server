package com.osrs.game.actor

import com.osrs.common.location.Location
import com.osrs.game.actor.render.Renderer

abstract class Actor {
    abstract val renderer: Renderer
    abstract var location: Location
    var lastLocation: Location? = null
    var index = 0

    fun pendingUpdates() = renderer.updates
    fun hasPendingUpdate() = renderer.hasPendingUpdate()

    fun reset() {
        renderer.clearUpdates()
    }
}
