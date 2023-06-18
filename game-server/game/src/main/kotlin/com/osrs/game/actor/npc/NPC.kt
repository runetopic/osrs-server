package com.osrs.game.actor.npc

import com.osrs.api.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.world.World

class NPC(
    val id: Int,
    world: World,
    val spawnLocation: Location
) : Actor(world) {
    override fun login() {
        this.initialize(spawnLocation)
        online = true
    }

    override fun logout() {
        zone.leaveZone(this)
        online = false
    }

    override fun totalHitpoints(): Int = 100

    override fun currentHitpoints(): Int = 100

    override fun processMovement() {
        if (id == 3216) wander(1)
        movementQueue.process(this)
    }
}
