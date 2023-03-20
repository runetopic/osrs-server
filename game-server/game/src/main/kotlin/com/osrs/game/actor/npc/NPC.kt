package com.osrs.game.actor.npc

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.world.World

class NPC(
    val id: Int,
    world: World,
    private val spawnLocation: Location
) : Actor(world) {
    override fun login() {
        super.initialize(spawnLocation)
        updateMap(true)
        online = true
    }

    override fun logout() {
        zone.leaveZone(this)
        online = false
    }

    override fun totalHitpoints(): Int = 100

    override fun currentHitpoints(): Int = 100
}
