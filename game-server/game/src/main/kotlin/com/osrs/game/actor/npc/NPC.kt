package com.osrs.game.actor.npc

import com.osrs.api.map.location.Location
<<<<<<< HEAD
import com.osrs.api.resource.NPCConfig
=======
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
import com.osrs.game.actor.Actor
import com.osrs.game.world.World

class NPC(
    val id: Int,
    val config: NPCConfig,
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
<<<<<<< HEAD
        wander(config.wander) // TODO: setup wander config
=======
        if (id == 3216) wander(1)
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
        movementQueue.process(this)
    }
}
