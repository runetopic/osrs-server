package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementSpeed
import com.osrs.game.actor.render.impl.MovementSpeedType
import com.osrs.game.actor.render.impl.TemporaryMovementSpeed

class ActorRenderer {

    val pendingUpdates = mutableListOf<RenderType>()

    fun appearance(appearance: Appearance): Appearance {
        pendingUpdates += appearance
        return appearance
    }

    fun updateMovementSpeed(type: MovementSpeedType) {
        pendingUpdates += MovementSpeed(type)
    }

    fun temporaryMovementSpeed(type: MovementSpeedType) {
        pendingUpdates += TemporaryMovementSpeed(type)
    }

    fun hasPendingUpdate(): Boolean = pendingUpdates.isNotEmpty()

    fun clearUpdates() = pendingUpdates.removeIf {
        it !is Appearance
    }
}
