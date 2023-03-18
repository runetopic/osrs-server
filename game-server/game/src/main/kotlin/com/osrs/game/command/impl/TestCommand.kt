package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.FaceActor
import com.osrs.game.command.CommandListener

@Singleton
class TestCommand : CommandListener(
    name = "test"
) {
    override fun execute(command: String, player: Player) {
        val p2 = player.world.players[2] ?: return

        player.message("Player ${p2.displayName} ${p2.index}")

        player.message("World count ${player.world.players.size}")

        player.renderer.update(FaceActor(p2))
    }
}
