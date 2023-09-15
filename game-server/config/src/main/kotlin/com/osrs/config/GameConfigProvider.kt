package com.osrs.config

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.config.ApplicationConfig
import java.nio.file.Path

class GameConfigProvider @Inject constructor(
    val config: ApplicationConfig
) : Provider<ServerConfig> {
    override fun get(): ServerConfig = ServerConfig(
        local = config.property("game.local").getString().toBoolean(),
        benchmarking = config.property("game.benchmarking").getString().toBoolean(),
        build = BuildConfig(
            major = config.property("game.build.major").getString().toInt(),
            minor = config.property("game.build.minor").getString().toInt(),
            token = config.property("game.build.token").getString()
        ),
        cache =  CacheConfig(
            server = Path.of(config.property("game.cache.server").getString()),
            client = Path.of(config.property("game.cache.client").getString()),
        ),
        resources = GameResources(
            players = Path.of(config.property("game.resources.players").getString()),
            npcs = Path.of(config.property("game.resources.npcs").getString()),
            ui = Path.of(config.property("game.resources.ui").getString()),
            xteas = Path.of(config.property("game.resources.xteas").getString()),
        )
    )
}
