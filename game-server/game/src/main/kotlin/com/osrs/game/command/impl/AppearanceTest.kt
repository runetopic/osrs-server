package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.refreshAppearance
import com.osrs.game.command.CommandListener

@Singleton
class AppearanceTest : CommandListener(
    name = "appearance"
) {
    override fun execute(command: String, player: Player) {
        val nextAppearance = player.appearance.copy(headIcon = 2)
        player.refreshAppearance(nextAppearance)
    }
}
