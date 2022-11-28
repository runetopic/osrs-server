package com.osrs.game

import com.google.inject.Inject
import com.osrs.game.tick.GameTick
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Game @Inject constructor(
    private val gameTick: GameTick,
) {
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    fun start() {
        executorService.scheduleAtFixedRate(gameTick, 600, 600, TimeUnit.MILLISECONDS)
    }
}
