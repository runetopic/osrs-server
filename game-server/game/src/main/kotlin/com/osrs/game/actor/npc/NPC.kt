package com.osrs.game.actor.npc

import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.world.World

class NPC(
    world: World
) : Actor(world) {
    override var moveDirection: MoveDirection?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun totalHitpoints(): Int {
        TODO("Not yet implemented")
    }

    override fun currentHitpoints(): Int {
        TODO("Not yet implemented")
    }
}
