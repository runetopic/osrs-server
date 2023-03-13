package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.SpotAnimation
import com.osrs.game.command.CommandListener

@Singleton
class SpotAnimationCommand : CommandListener(
    name = "spot_anim",
) {
    override fun execute(command: String, player: Player) {
        player.renderer.update(
            SpotAnimation(
                id = 320,
            ),
        )
    }
}
