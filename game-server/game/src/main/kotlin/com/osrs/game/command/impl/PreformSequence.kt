package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.Sequence
import com.osrs.game.command.CommandListener
import java.lang.NumberFormatException

@Singleton
class PreformSequence : CommandListener(
    name = "sequence"
) {
    override fun execute(player: Player, command: String, arguments: List<String>) {
        try {
            val sequenceId = arguments.firstOrNull()?.toInt() ?: return player.invalidSyntaxMessage("Please use syntax: ::sequence 1250")

            player.renderer.update(
                Sequence(
                    id = sequenceId
                )
            )
        } catch (exception: NumberFormatException) {
            player.invalidSyntaxMessage("Please use syntax: ::sequence 1250")
        }
    }
}
