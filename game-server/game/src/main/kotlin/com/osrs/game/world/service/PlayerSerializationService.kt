package com.osrs.game.world.service

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.database.dto.UpdateAccountRequest
import com.osrs.game.actor.player.Player
import com.osrs.game.world.World
import com.osrs.service.account.AccountService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PlayerSerializationService @Inject constructor(
    private val accountService: AccountService
) {
    private val logger = InlineLogger()

    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun start(world: World) {
        executor.scheduleWithFixedDelay({
            if (!world.isOnline) return@scheduleWithFixedDelay

            logger.info { "Saving ${world.players.size} players..." }

            for (player in world.players.parallelStream()) {
                if (player == null || !player.online) continue

                accountService.saveAccount(
                    UpdateAccountRequest(
                        userName = player.username,
                        displayName = player.displayName,
                        skills = player.skills,
                        location = player.location,
                        objs = player.objs
                    )
                )
            }
        }, 0, 15, TimeUnit.MINUTES)
    }

    fun savePlayer(player: Player) {
        executor.submit {
            accountService.saveAccount(
                UpdateAccountRequest(
                    userName = player.username,
                    displayName = player.displayName,
                    skills = player.skills,
                    location = player.location,
                    objs = player.objs
                )
            )
        }
    }

    fun shutdown() = executor.shutdown()
}
