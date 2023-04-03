package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.FaceAngle
import com.osrs.game.command.CommandListener

@Singleton
class FaceAngleTest : CommandListener(
    name = "face_angle"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        player.renderer.update(
            FaceAngle(
                angle = 512
            )
        )
    }
}
