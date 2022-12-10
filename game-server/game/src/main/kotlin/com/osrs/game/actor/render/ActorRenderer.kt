package com.osrs.game.actor.render

/**
 * @author Tyler Telis
 * @email <xlitersps@gmail.com>
 * @author Jordan Abraham
 */
class ActorRenderer {

    val pendingUpdates = mutableListOf<Render>()

    fun appearance(appearance: Render.Appearance): Render.Appearance {
        pendingUpdates += appearance
        return appearance
    }

    fun hasPendingUpdate(): Boolean = pendingUpdates.isNotEmpty()
    fun clearUpdates() = pendingUpdates.clear()
}
