package com.osrs.game.command.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.message
import com.osrs.game.command.CommandListener
import com.osrs.game.world.World

@Singleton
class Players @Inject constructor(
    val world: World
) : CommandListener(
    name = "players"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        player.message("Player count: ${this@Players.world.players.size}")
    }
}
