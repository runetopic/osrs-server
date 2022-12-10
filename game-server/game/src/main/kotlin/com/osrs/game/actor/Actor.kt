package com.osrs.game.actor

import com.osrs.common.location.Location
import com.osrs.game.actor.render.ActorRenderer

abstract class Actor {
    protected val renderer = ActorRenderer()
    abstract var location: Location
    var lastLocation: Location? = null
    var index = 0

    fun pendingUpdates() = renderer.pendingUpdates
    fun hasPendingUpdate() = renderer.hasPendingUpdate()

    fun reset() {
        renderer.clearUpdates()
    }
}
