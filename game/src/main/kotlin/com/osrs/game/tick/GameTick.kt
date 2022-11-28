package com.osrs.game.tick

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.world.World
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class GameTick(private val world: World) : Runnable {
    private val logger = InlineLogger()
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    private var tick = 0

    fun start() {
        executorService.scheduleAtFixedRate(this, 600, 600, TimeUnit.MILLISECONDS)
    }

    override fun run() {
        val time = measureTimeMillis {
            ++tick
        }

        logger.info { "Game Tick #$tick Players=${world.players.size} took $time ms." }
    }
}
