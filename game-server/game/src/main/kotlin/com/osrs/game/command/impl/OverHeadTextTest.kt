package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.OverHeadText
import com.osrs.game.command.CommandListener

@Singleton
class OverHeadTextTest : CommandListener(
    name = "overhead_text"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        player.renderer.update(
            OverHeadText(
                text = "Testing"
            )
        )
    }
}
