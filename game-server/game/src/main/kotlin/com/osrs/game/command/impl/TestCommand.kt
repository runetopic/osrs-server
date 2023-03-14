package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.game.actor.hit.HealthBar.DEFAULT
import com.osrs.game.actor.hit.HitSplat
import com.osrs.game.actor.hit.HitType.REGULAR_DAMAGE
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
                splats = listOf(
                    HitSplat(
                        source = player,
                        type = REGULAR_DAMAGE,
                        amount = 10,
                        delay = 0
                    ),
                    HitSplat(
                        source = player,
                        type = REGULAR_DAMAGE,
                        amount = 10,
                        delay = 0
                    ),
                    HitSplat(
                        source = player,
                        type = REGULAR_DAMAGE,
                        amount = 10,
                        delay = 0
                    ),
                    HitSplat(
                        source = player,
                        type = REGULAR_DAMAGE,
                        amount = 10,
                        delay = 0
                    )
                ),
                bars = listOf(
                    DEFAULT,
                    DEFAULT
                )
            )
        )
    }
}
