package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener

@Singleton
class ZoneCommand : CommandListener(
    name = "zone_info"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.world.zone(player.location)

        player.message("Current items in the zone: ${zone.objs.size}")
    }
}
