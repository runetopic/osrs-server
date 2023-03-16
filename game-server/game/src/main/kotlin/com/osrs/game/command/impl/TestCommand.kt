package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.common.map.location.transform
import com.osrs.game.actor.hit.HealthBar.Companion.Default
import com.osrs.game.actor.hit.HitSplat
import com.osrs.game.actor.hit.HitType.Companion.Normal
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.ExactMove
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

        player.renderer.update(
            ExactMove(
                currentLocation = player.location,
                firstLocation = player.location.transform(0, -5, 0),
                firstDuration = 20,
                angle = 512
            )
        )
    }
}
