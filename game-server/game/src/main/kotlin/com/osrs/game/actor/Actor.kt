package com.osrs.game.actor

import com.osrs.common.map.location.Location
import com.osrs.game.actor.movement.Direction
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.actor.render.ActorRenderer
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.Zone

abstract class Actor {
    var isRunning = true
    var runEnergy: Float = 10_000f
    abstract var moveDirection: MoveDirection?
    val renderer = ActorRenderer()
    abstract var location: Location
    var lastLocation: Location = Location.None
    var index = 0
    abstract var world: World
    abstract var zone: Zone

    var zones = hashSetOf<Int>()

    fun canTravel(location: Location, direction: Direction) = world.collisionMap.canTravel(location, direction)

    fun reset() {
        renderer.clearUpdates()
        moveDirection = null
        lastLocation = location
    }

    abstract fun totalHitpoints(): Int
    abstract fun currentHitpoints(): Int
}
