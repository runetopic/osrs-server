package com.osrs.game.actor.render

abstract class Renderer(
    val updates: RenderBlockBuilderCollection = RenderBlockBuilderCollection()
) {
    fun hasPendingUpdate(): Boolean = updates.isNotEmpty()
    fun clearUpdates() = updates.clear()
}
