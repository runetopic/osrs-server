package com.osrs.game.ui.listener.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.ui.UserInterface.Inventory
import com.osrs.game.ui.listener.InterfaceListener

@Singleton
class InventoryInterfaceListener : InterfaceListener<Inventory> {

    override fun onOpen(player: Player, userInterface: Inventory) {
        player.inventory?.refresh()
    }
}
