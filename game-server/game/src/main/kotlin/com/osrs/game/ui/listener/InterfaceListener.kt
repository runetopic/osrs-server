package com.osrs.game.ui.listener

import com.osrs.game.actor.player.Player
import com.osrs.game.ui.UserInterface

interface InterfaceListener<out T : UserInterface> {
    fun onOpen(player: Player, userInterface: @UnsafeVariance T) {}
}
