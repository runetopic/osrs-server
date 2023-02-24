package com.osrs.game.command.impl

import com.google.inject.Singleton
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.transform
import com.osrs.game.actor.Actor
import com.osrs.game.actor.player.Player
import com.osrs.game.command.CommandListener
import com.osrs.game.projectile.Projectile

class ProjectileRequest(
    val target: Actor? = null,
    val from: Location,
    val to: Location = Location.None,
    val projectile: Projectile
)

@Singleton
class ProjectileTest : CommandListener(
    name = "proj"
) {
    override fun execute(command: String, player: Player) {
        val zone = player.zone

        player.message("Zone coords ${zone.location}")

        val projectileRequest = ProjectileRequest(
            from = player.location,
            to = player.location.transform(1, 0, 0),
            projectile = Projectile(
                id = 2281,
                startHeight = 35,
                endHeight = 36,
                delay = 15,
                angle = 127,
                lengthAdjustment = -3,
                distOffset = 11,
                stepMultiplier = 4
            )
        )

        zone.requestAddMapProjAnim(projectileRequest)
    }
}
