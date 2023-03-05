package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.common.map.location.Location
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.item.FloorItem
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest

@Singleton
class AddItemTest : CommandListener(
    name = "drop_item"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.world.zone(Location(3222, 3222, 0))

        val floorItem =  FloorItem(995, Int.MAX_VALUE, Location(3222, 3222, 0))

        player.objs.add(floorItem)

        zone.update(
            ObjAddRequest(
                floorItem,
                player.index
            )
        )
    }
}
