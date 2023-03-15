package com.osrs.game.actor.npc

import com.osrs.common.map.location.Location
import com.osrs.game.actor.Actor
import com.osrs.game.actor.movement.MoveDirection
import com.osrs.game.world.World
import com.osrs.game.world.map.zone.Zone

class NPC : Actor() {
    override var moveDirection: MoveDirection?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var location: Location
        get() = TODO("Not yet implemented")
        set(value) {}
    override var world: World
        get() = TODO("Not yet implemented")
        set(value) {}
    override var zone: Zone
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun totalHitpoints(): Int {
        TODO("Not yet implemented")
    }

    override fun currentHitpoints(): Int {
        TODO("Not yet implemented")
    }
}
