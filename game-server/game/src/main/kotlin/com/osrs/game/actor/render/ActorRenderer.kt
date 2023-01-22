package com.osrs.game.actor.render

import com.osrs.game.actor.render.impl.Appearance
import com.osrs.game.actor.render.impl.MovementType
import com.osrs.game.actor.render.impl.TemporaryMovementType

class ActorRenderer {

    val pendingUpdates = mutableListOf<RenderType>()

    fun appearance(appearance: Appearance): Appearance {
        pendingUpdates += appearance
        return appearance
    }

    fun updateMovementType(type: Int) {
        pendingUpdates += MovementType(type)
    }

    fun temporaryMovementType(type: Int) {
        pendingUpdates += TemporaryMovementType(type)
    }

    fun hasPendingUpdate(): Boolean = pendingUpdates.isNotEmpty()
    fun clearUpdates() = pendingUpdates.clear()
}
