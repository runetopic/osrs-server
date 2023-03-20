package com.osrs.game.tick

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.game.network.packet.builder.impl.render.NPCUpdateBlocks
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.world.World
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Singleton
class GameTickProvider @Inject constructor(
    private val world: World,
    private val playerUpdateBlocks: PlayerUpdateBlocks,
    private val npcUpdateBlocks: NPCUpdateBlocks
) : Provider<GameTick> {
    private val executorService = Executors.newSingleThreadScheduledExecutor()

    override fun get(): GameTick {
        val gameTick = GameTick(world, playerUpdateBlocks, npcUpdateBlocks)
        executorService.scheduleAtFixedRate(gameTick, 600, 600, TimeUnit.MILLISECONDS)
        return gameTick
    }
}
