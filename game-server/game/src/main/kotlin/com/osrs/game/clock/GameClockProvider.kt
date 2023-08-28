package com.osrs.game.clock

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.world.World
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Singleton
class GameClockProvider @Inject constructor(
    private val world: World,
    private val playerUpdateBlocks: PlayerUpdateBlocks,
    private val npcUpdateBlocks: NPCUpdateBlocks
) : Provider<GameClock> {
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    override fun get(): GameClock {
        val gameTick = GameClock(world, playerUpdateBlocks, npcUpdateBlocks)
        executorService.scheduleAtFixedRate(gameTick, 600, 600, TimeUnit.MILLISECONDS)
        return gameTick
    }
}
