package com.osrs.game.actor.npc

import org.rsmod.pathfinder.RouteCoordinates
import java.lang.Math.random
import kotlin.math.roundToInt

fun NPC.walkTo(routeCoordinates: RouteCoordinates) {
    movementQueue.appendRoute(routeCoordinates)
}

fun NPC.wander(radius: Int = 5) {
    val xOffset = (-radius + random() * 10.0).roundToInt()
    val zOffset = (random() * 10.0 - radius).roundToInt()
    val location = RouteCoordinates(location.x + xOffset, location.z + zOffset)
    walkTo(location)
}
