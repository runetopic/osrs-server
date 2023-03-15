package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.hit.HealthBar.Companion.Default
import com.osrs.game.actor.hit.HitSplat
import com.osrs.game.actor.hit.HitType.Companion.Normal
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.HealthUpdate
import com.osrs.game.command.CommandListener

@Singleton
class TestCommand : CommandListener(
    name = "test"
) {
    override fun execute(command: String, player: Player) {
        player.renderer.update(
            HealthUpdate(
                source = player,
                splats = arrayOf(
                    HitSplat(
                        source = player,
                        type = Normal,
                        amount = 10,
                        delay = 0
                    ),
                    HitSplat(
                        source = player,
                        type = Normal,
                        amount = 10,
                        delay = 5
                    ),
                    HitSplat(
                        source = player,
                        type = Normal,
                        amount = 10,
                        delay = 10
                    ),
                    HitSplat(
                        source = player,
                        type = Normal,
                        amount = 10,
                        delay = 15
                    )
                ),
                bars = arrayOf(
                    Default
                )
            )
        )
    }
}
