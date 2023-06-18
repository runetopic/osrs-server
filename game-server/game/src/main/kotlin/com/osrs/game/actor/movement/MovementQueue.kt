package com.osrs.game.actor.movement

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.MovementSpeed
import org.rsmod.pathfinder.Route
import org.rsmod.pathfinder.RouteCoordinates
import java.util.*
import kotlin.math.sign

class MovementQueue(
    private val steps: Deque<Location> = LinkedList()
) : Deque<Location> by steps {
    val routeSteps: Deque<RouteCoordinates> = LinkedList()

    fun process(actor: Actor) {
        calculateNextTravelPoint(actor)

        var nextWalkStep = poll() ?: return

        val walkDirection: Direction = Direction.to(actor.location, nextWalkStep)
        var runDirection: Direction? = null

        if (walkDirection == Direction.NONE || !actor.canTravel(actor.location, walkDirection)) return

        var location = nextWalkStep

        if (actor.isRunning) {
            calculateNextTravelPoint(actor, location)

            nextWalkStep = poll() ?: return run {
                actor.renderer.update(MovementSpeed(type = MovementType.WALK, temporary = true))
                actor.moveTo(location, MoveDirection(walkDirection, null))
            }

            runDirection = Direction.to(location, nextWalkStep)

            if (!actor.canTravel(location, runDirection)) {
                clear()
                runDirection = null
            } else {
                location = nextWalkStep
            }
        }

        if (runDirection != null) {
            actor.renderer.update(MovementSpeed(MovementType.RUN, temporary = true))
        }

        actor.moveTo(location, MoveDirection(walkDirection, runDirection))
    }

    private fun calculateNextTravelPoint(actor: Actor, currentLocation: Location = actor.location) {
        if (isNotEmpty() || routeSteps.isEmpty()) return
        clear()
        val step = routeSteps.poll()
        var currentX = currentLocation.x
        var currentZ = currentLocation.z
        val destX = step.x
        val destY = step.y
        val xSign = (destX - currentX).sign
        val ySign = (destY - currentZ).sign
        var count = 0
        while (currentX != destX || currentZ != destY) {
            currentX += xSign
            currentZ += ySign
            add(Location(currentX, currentZ, currentLocation.level))
            val maxTurns = if (actor is Player) MAX_TURNS_PLAYER else MAX_TURNS_NPC
            if (++count > maxTurns) break
        }
    }

    fun appendRoute(route: Route) {
        routeSteps.clear()
        clear()
        routeSteps.addAll(route)
    }

    fun appendRoute(routeCoordinates: RouteCoordinates) {
        routeSteps.clear()
        clear()
        routeSteps.add(routeCoordinates)
    }

    fun hasWalkSteps(): Boolean = routeSteps.isNotEmpty()

    companion object {
        private const val MAX_TURNS_PLAYER = 25
        private const val MAX_TURNS_NPC = 10
    }
}
