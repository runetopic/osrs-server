package com.osrs.game.tick

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.tick.task.PlayerSyncTask
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.world.World
import kotlin.system.measureTimeMillis

class GameTick(private val world: World) : Runnable {
    private val logger = InlineLogger()
    private var tick = 0

    private val syncTask = setOf(
        PlayerSyncTask(world)
    )

    override fun run() {
        try {
            val time = measureTimeMillis {
                ++tick
                world.processLoginRequest()
                syncTask.forEach(SyncTask::sync)
            }
            logger.info { "Game Tick #$tick took $time ms. Players=${world.players.size}" }
        } catch (exception: Exception) {
            logger.error(exception) { "Error occurred during game tick #$tick" }
        }
    }
}
