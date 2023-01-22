package com.osrs.game.actor.movement

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.MoveDirection
import com.osrs.game.actor.render.impl.MovementSpeedType
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
        appendNextStep(actor.location)

        var nextWalkStep = poll()

        if (nextWalkStep == null) {
            clear()
            return
        }

        val walkDirection: Direction = Direction.to(actor.location, nextWalkStep)
        var runDirection: Direction? = null

        if (walkDirection == Direction.NONE) {
            clear()
            return
        }

        var location = nextWalkStep

        if (actor.isRunning) {
            appendNextStep(location)

            nextWalkStep = poll() ?: return run {
                actor.renderer.temporaryMovementSpeed(MovementSpeedType.WALK)
                moveTo(location, MoveDirection(walkDirection, null))
            }

            runDirection = Direction.to(location, nextWalkStep)
            location = nextWalkStep
        }

        moveTo(location, MoveDirection(walkDirection, runDirection))

        if (runDirection != null) {
            actor.renderer.temporaryMovementSpeed(MovementSpeedType.RUN)
        }
    }

    private fun moveTo(
        location: Location,
        direction: MoveDirection
    ) {
        actor.location = location
        actor.moveDirection = direction
    }

    private fun appendNextStep(location: Location) {
        if (isNotEmpty()) return
        if (routeSteps.isEmpty()) return
        clear()
        val step = routeSteps.poll()
        var curX = location.x
        var curY = location.z
        val destX = step.x
        val destY = step.y
        val xSign = (destX - curX).sign
        val ySign = (destY - curY).sign
        var count = 0
        while (curX != destX || curY != destY) {
            curX += xSign
            curY += ySign
            add(Location(curX, curY))
            if (++count > MAX_TURNS) break
        }
    }

    fun appendRoute(route: Route) {
        routeSteps.clear()
        clear()
        routeSteps.addAll(route)
    }

    companion object {
        private const val MAX_TURNS = 25
    }
}
