package com.osrs.game.command

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.message

abstract class CommandListener(val name: String) {
    abstract fun execute(player: Player, command: String, arguments: List<String>)

    fun Player.invalidSyntaxMessage(message: String) {
        message("<col=DC143C>$message</col>")
    }
}
