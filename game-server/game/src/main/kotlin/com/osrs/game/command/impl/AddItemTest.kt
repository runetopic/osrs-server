package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.item.FloorItem

@Singleton
class AddItemTest : CommandListener(
    name = "drop_item"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.world.zone(player.location)
        zone.requestAddObj(FloorItem(995, 1,  player.location))
    }
}
