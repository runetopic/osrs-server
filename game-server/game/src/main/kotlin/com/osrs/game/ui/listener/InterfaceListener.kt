package com.osrs.game.ui.listener

import com.osrs.game.actor.player.Player
import com.osrs.game.ui.UserInterface

interface InterfaceListener<out T : UserInterface> {
    fun opened(player: Player, userInterface: @UnsafeVariance T) {}
    fun clicked(player: Player, childId: Int, userInterface: @UnsafeVariance T) {}
    fun resumeStringDialogue(player: Player, input: String) {}
}
