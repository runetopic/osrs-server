package com.osrs.game.command

import com.osrs.game.actor.player.Player

abstract class CommandListener(val name: String) {
    abstract fun execute(command: String, player: Player)
}
