package com.osrs.game.world.service

import com.google.inject.Inject
import com.osrs.database.dto.UpdateAccountRequest
import com.osrs.database.service.AccountService
import com.osrs.game.actor.player.Player
import java.util.concurrent.Executors

class PlayerSerializationService @Inject constructor(
    private val accountService: AccountService
) {
    private val executor = Executors.newSingleThreadScheduledExecutor()

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
