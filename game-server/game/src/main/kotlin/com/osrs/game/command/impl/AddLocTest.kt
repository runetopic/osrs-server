package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.world.map.GameObject
import com.osrs.game.world.map.zone.ZoneUpdateRequest.LocAddRequest

@Singleton
class AddLocTest : CommandListener(
    name = "spawn_loc"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.zone
        val loc = GameObject(1124, player.location, 22, 0)
        player.world.collisionMap.addObjectCollision(loc)
        // TODO collision map should be static imo
        zone.update(LocAddRequest(loc))
    }
}
