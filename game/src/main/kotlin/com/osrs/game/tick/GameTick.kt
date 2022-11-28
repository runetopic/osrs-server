package com.osrs.game.tick

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.world.World
import kotlin.system.measureTimeMillis

class GameTick(private val world: World) : Runnable {
    private val logger = InlineLogger()

    private var tick = 0

    override fun run() {
        val time = measureTimeMillis {
            ++tick
        }

        logger.info { "Game Tick #$tick Players=${world.players.size} took $time ms." }
    }
}
