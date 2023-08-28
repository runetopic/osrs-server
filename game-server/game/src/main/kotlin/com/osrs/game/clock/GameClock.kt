package com.osrs.game.clock

import com.github.michaelbull.logging.InlineLogger
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.clock.task.WorldSyncTask
import com.osrs.game.world.World
import kotlin.system.measureTimeMillis

class GameClock(
    private val world: World,
    playerUpdateBlocks: PlayerUpdateBlocks,
    npcUpdateBlocks: NPCUpdateBlocks
) : Runnable {
    private val logger = InlineLogger()
    private var tick = 0

    private val syncTask = setOf(
        WorldSyncTask(world, playerUpdateBlocks, npcUpdateBlocks)
    )

    override fun run() {
        if (!world.isOnline) return

        try {
            val time = measureTimeMillis {
                ++tick
                for (task in syncTask) {
                    task.sync(tick)
                }
            }
            val freeMemoryMB = ((Runtime.getRuntime().freeMemory() / 1024) / 1024).toFloat()
            val totalMemoryMB = ((Runtime.getRuntime().totalMemory() / 1024) / 1024).toFloat()
            val maxMemoryMB = ((Runtime.getRuntime().maxMemory() / 1024) / 1024).toFloat()
            val allocatedMemoryMB = (totalMemoryMB - freeMemoryMB)
            val freeMemory = (maxMemoryMB - allocatedMemoryMB)
            val usedPercentage = (allocatedMemoryMB / maxMemoryMB) * 100
            logger.debug { "Game Tick #$tick took $time ms. Players=${world.players.size} Npcs=${world.npcs.size} Memory=(Max: $maxMemoryMB MB Allocated: $allocatedMemoryMB MB Free: $freeMemory MB Used: $usedPercentage%)" }
        } catch (exception: Exception) {
            logger.error(exception) { "Error occurred during game tick #$tick" }
        }
    }

    fun current() = tick
}
