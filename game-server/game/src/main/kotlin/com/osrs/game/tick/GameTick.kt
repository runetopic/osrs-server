package com.osrs.game.tick

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.network.packet.builder.impl.sync.PlayerUpdateBlocks
import com.osrs.game.tick.task.SyncTask
import com.osrs.game.tick.task.player.PlayerSyncTask
import com.osrs.game.tick.task.world.WorldSyncTask
import com.osrs.game.world.World
import kotlin.system.measureTimeMillis


class GameTick(
    private val world: World,
    playerUpdateBlocks: PlayerUpdateBlocks
) : Runnable {
    private val logger = InlineLogger()
    private var tick = 0

    private val syncTask = setOf(
        WorldSyncTask(world),
        PlayerSyncTask(world, playerUpdateBlocks)
    )

    override fun run() {
        if (!world.isOnline) return

        try {
            val time = measureTimeMillis {
                syncTask.forEach(SyncTask::sync)
                ++tick
            }
            val freeMemoryMB = ((Runtime.getRuntime().freeMemory() / 1024) / 1024).toFloat()
            val totalMemoryMB = ((Runtime.getRuntime().totalMemory() / 1024) / 1024).toFloat()
            val maxMemoryMB = ((Runtime.getRuntime().maxMemory() / 1024) / 1024).toFloat()
            val allocatedMemoryMB = (totalMemoryMB - freeMemoryMB)
            val freeMemory = (maxMemoryMB - allocatedMemoryMB)
            val usedPercentage = (allocatedMemoryMB / maxMemoryMB) * 100
            logger.info { "Game Tick #$tick took $time ms. Players=${world.players.size} Memory=(Max: $maxMemoryMB MB Allocated: $allocatedMemoryMB MB Free: $freeMemory MB Used: $usedPercentage%)" }
        } catch (exception: Exception) {
            logger.error(exception) { "Error occurred during game tick #$tick" }
        }
    }
}
