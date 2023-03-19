package com.osrs.game.actor.npc

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.world.World

class NPC(
    val id: Int,
    world: World,
    location: Location
) : Actor(world, location) {
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
