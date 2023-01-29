package com.osrs.game.actor

import com.osrs.common.map.location.Location
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.render.ActorRenderer
import com.osrs.game.world.World

data class MoveDirection(val walkDirection: Direction?, val runDirection: Direction?)

abstract class Actor {
    var isRunning = true
    abstract var moveDirection: MoveDirection?
    val renderer = ActorRenderer()
    abstract var location: Location
    var lastLocation: Location? = null
    var index = 0
    abstract var world: World

    fun pendingUpdates() = renderer.pendingUpdates

    fun canTravel(location: Location, direction: Direction) = world.collisionMap.canTravel(location, direction)

    fun reset() {
        renderer.clearUpdates()
        moveDirection = null
        lastLocation = location
    }
}
