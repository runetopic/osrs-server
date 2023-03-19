package com.osrs.game.actor.npc

import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.world.World

class NPC(
    world: World
) : Actor(world) {
    override var moveDirection: MoveDirection? = null

    override fun login() {
        updateMap(true)
    }

    override fun logout() {
        zone.leaveZone(this)
        online = false
    }

    override fun totalHitpoints(): Int = 100

    override fun currentHitpoints(): Int = 100
}
