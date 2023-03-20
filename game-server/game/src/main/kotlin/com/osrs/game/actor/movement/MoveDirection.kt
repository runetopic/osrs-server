package com.osrs.game.actor.movement

data class MoveDirection(
    val walkDirection: Direction?,
    val runDirection: Direction?
) {
    companion object {
        val None = MoveDirection(null, null)
    }
}
