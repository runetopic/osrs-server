package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.common.map.location.transform
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.player.message
import com.osrs.game.command.CommandListener
import com.osrs.game.projectile.Projectile
import com.osrs.game.world.map.zone.ZoneUpdateRequest.ProjectileRequest

@Singleton
class ProjectileTest : CommandListener(
    name = "proj"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.zone

        player.message("Zone coords ${zone.location}")

        val projectileRequest = ProjectileRequest(
            from = player.location,
            to = player.location.transform(12, 0, 0),
            projectile = Projectile(
                id = 1465,
                startHeight = 35,
                endHeight = 36,
                delay = 12,
                angle = 127,
                lengthAdjustment = -3,
                distOffset = 11,
                stepMultiplier = 4
            )
        )

        val projectileRequest2 = ProjectileRequest(
            from = player.location,
            to = player.location.transform(12, 0, 0),
            projectile = Projectile(
                id = 1465,
                startHeight = 35,
                endHeight = 36,
                delay = 20,
                angle = 127,
                lengthAdjustment = -3,
                distOffset = 11,
                stepMultiplier = 4
            )
        )

        zone.update(projectileRequest)
        zone.update(projectileRequest2)
    }
}
