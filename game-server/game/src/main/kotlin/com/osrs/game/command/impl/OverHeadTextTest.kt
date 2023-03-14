package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.OverHeadText
import com.osrs.game.command.CommandListener

@Singleton
class OverHeadTextTest : CommandListener(
    name = "overhead_text",
) {
    override fun execute(command: String, player: Player) {
        player.renderer.update(
            OverHeadText(
                text = "Testing",
            ),
        )
    }
}
