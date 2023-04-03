package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.ZoneLocation
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest

@Singleton
class MassAddItemTest : CommandListener(
    name = "mass_drop_item"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        // Looping 8x8 is too much for the write channel byte pool.
        player.zones.forEach {
            repeat(7) { x ->
                repeat(7) { z ->
                    val zone = player.world.zone(ZoneLocation(it))
                    zone.update(ObjAddRequest(FloorItem(4151, 1, Location(zone.location.location.x + x, zone.location.location.z + z), 100)))
                }
            }
        }
    }
}
