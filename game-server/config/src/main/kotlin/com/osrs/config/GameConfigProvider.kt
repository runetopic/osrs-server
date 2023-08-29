package com.osrs.config

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.config.ApplicationConfig
import java.nio.file.Path

class GameConfigProvider @Inject constructor(
    val config: ApplicationConfig
) : Provider<GameConfig> {
    override fun get(): GameConfig = GameConfig(
        local = config.property("game.local").getString().toBoolean(),
        benchmarking = config.property("game.benchmarking").getString().toBoolean(),
        build = BuildConfig(
            major = config.property("game.build.major").getString().toInt(),
            minor = config.property("game.build.major").getString().toInt(),
            token = config.property("game.build.token").getString()
        ),
        cache =  CacheConfig(
            vanilla = Path.of(config.property("game.cache.vanilla").getString()),
            game = Path.of(config.property("game.cache.game").getString()),
            js5 = Path.of(config.property("game.cache.js5").getString())
        )
    )
}
