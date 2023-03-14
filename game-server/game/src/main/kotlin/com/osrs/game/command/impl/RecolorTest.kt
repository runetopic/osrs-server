package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.Recolor
import com.osrs.game.command.CommandListener

@Singleton
class RecolorTest : CommandListener(
    name = "recolor"
) {
    override fun execute(command: String, player: Player) {
        player.renderer.update(
            Recolor(
                hue = 0,
                saturation = 6,
                luminance = 28,
                opacity = 112,
                delay = 0,
                duration = 240
            )
        )
    }
}
