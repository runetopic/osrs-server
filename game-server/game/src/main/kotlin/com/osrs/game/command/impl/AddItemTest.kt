package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.api.item.FloorItem
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjAddRequest

@Singleton
class AddItemTest : CommandListener(
    name = "drop_item"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        val floorItem = FloorItem(995, Int.MAX_VALUE, player.location, 100)

        player.zone.update(
            player,
            ObjAddRequest(
                floorItem = floorItem
            )
        )
    }
}
