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

    fun updateMovementType() {
        pendingUpdates += MovementType()
    }

    fun temporaryMovementType() {
        pendingUpdates += TemporaryMovementType()
    }

    fun hasPendingUpdate(): Boolean = pendingUpdates.isNotEmpty()
    fun clearUpdates() = pendingUpdates.clear()
}
