package com.osrs.game.actor.render

import com.osrs.game.actor.Actor

abstract class Renderer<T : Actor>(
    val updates: RenderBlockBuilderCollection<T> = RenderBlockBuilderCollection()
) {
    fun hasPendingUpdate(): Boolean = updates.isNotEmpty()
    fun clearUpdates() = updates.clear()
}
