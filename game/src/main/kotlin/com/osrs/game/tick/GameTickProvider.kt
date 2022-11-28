package com.osrs.game.tick

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.game.world.World
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class GameTickProvider @Inject constructor(
    environment: ApplicationEnvironment,
    private val world: World
) : Provider<GameTick> {
    override fun get(): GameTick {
        return GameTick(world)
    }
}
