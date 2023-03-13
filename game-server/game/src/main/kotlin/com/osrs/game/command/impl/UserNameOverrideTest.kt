package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.UserNameOverride
import com.osrs.game.command.CommandListener

@Singleton
class UserNameOverrideTest : CommandListener(
    name = "username_override"
) {
    override fun execute(command: String, player: Player) {
        player.renderer.userNameOverride(
            UserNameOverride(
                prefix = "Testing",
                infix = "UserName",
                suffix = "Override"
            )
        )
    }
}
