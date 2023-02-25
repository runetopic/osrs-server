package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.item.FloorItem
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjUpdateRequest

@Singleton
class RemoveItemTest : CommandListener(
    name = "remove_item"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.world.zone(player.location)
        zone.update(ObjUpdateRequest(FloorItem(995, 1, player.location), remove = true))
        player.message("Items in the zone: ${zone.objs.size}")
        player.message("Zone count: ${player.zones.size}")
    }
}
