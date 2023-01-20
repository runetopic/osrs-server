package com.osrs.game.actor.movement

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import org.rsmod.pathfinder.Route
import org.rsmod.pathfinder.RouteCoordinates
import java.util.*
import kotlin.math.sign

class MovementQueue(
    val actor: Actor,
    private val steps: Deque<Location> = LinkedList()
) : Deque<Location> by steps {
    private val routeSteps: Deque<RouteCoordinates> = LinkedList()

    fun process() {
        if (isEmpty()) appendNextStep()
        val next = poll() ?: return
        actor.walkDirection = Direction.to(actor.location, next)
        actor.location = next
    }

    private fun appendNextStep() {
        if (routeSteps.isEmpty()) return
        clear()
        val step = routeSteps.poll()
        val last = actor.location
        var curX = last.x
        var curY = last.z
        val destX = step.x
        val destY = step.y
        val xSign = (destX - curX).sign
        val ySign = (destY - curY).sign
        var count = 0
        while (curX != destX || curY != destY) {
            curX += xSign
            curY += ySign
            add(Location(curX, curY))

            if (++count > MAX_STEPS_PER_TURN) break
        }
    }

    fun appendRoute(route: Route) {
        routeSteps.clear()
        clear()
        routeSteps.addAll(route)
    }

    companion object {
        private const val MAX_STEPS_PER_TURN = 25
    }
}
