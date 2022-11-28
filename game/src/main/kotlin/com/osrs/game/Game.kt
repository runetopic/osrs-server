package com.osrs.game

import com.google.inject.Inject
import com.osrs.game.tick.GameTick

class Game @Inject constructor(
    private val gameTick: GameTick,
) {

    fun start() {
        gameTick.start()
    }
}
