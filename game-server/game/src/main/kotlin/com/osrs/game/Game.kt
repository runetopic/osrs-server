package com.osrs.game

import com.google.inject.Inject
import com.osrs.game.world.service.PlayerSerializationService

class Game @Inject constructor(
    private val playerSerializationService: PlayerSerializationService,
) {
    fun start() {

    }

    fun shutdown() {
        playerSerializationService.shutdown()
    }
}
