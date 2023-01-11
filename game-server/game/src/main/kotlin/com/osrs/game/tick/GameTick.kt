package com.osrs.game.tick

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.tick.task.player.PlayerSyncTask
import com.osrs.game.tick.task.world.WorldSyncTask
import com.osrs.game.world.World
import kotlin.system.measureTimeMillis

class GameTick(
    private val world: World
) : Runnable {
    private val logger = InlineLogger()
    private var tick = 0

    private val syncTask = setOf(
        WorldSyncTask(world),
        PlayerSyncTask(world)
    )

    override fun run() {
        if (!world.isOnline) return

        try {
            val time = measureTimeMillis {
                syncTask.forEach(SyncTask::sync)
                ++tick
            }
            logger.info { "Game Tick #$tick took $time ms. Players=${world.players.size}" }
        } catch (exception: Exception) {
            logger.error(exception) { "Error occurred during game tick #$tick" }
        }
    }
}
