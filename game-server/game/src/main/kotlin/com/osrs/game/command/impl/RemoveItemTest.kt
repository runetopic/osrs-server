package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.api.item.FloorItem
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ObjRemoveRequest

@Singleton
class RemoveItemTest : CommandListener(
    name = "remove_item"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        player.zone.update(ObjRemoveRequest(FloorItem(995, Int.MAX_VALUE, player.location)))
    }
}
