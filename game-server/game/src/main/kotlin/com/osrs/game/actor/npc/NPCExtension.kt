package com.osrs.game.actor.npc

import com.osrs.api.map.location.Location
import com.osrs.api.map.location.asRouteCoordinates
import com.osrs.api.map.location.randomize

fun NPC.walkTo(location: Location) {
    val routeCoordinates = location.asRouteCoordinates()
    println("NPC is requesting to move: $routeCoordinates")
    movementQueue.appendRoute(routeCoordinates)
}

fun NPC.wander(radius: Int): Location {
    if ((0..7).random() != 0) return spawnLocation

    val offset = spawnLocation.randomize(radius)
    walkTo(offset)
    return offset
}
