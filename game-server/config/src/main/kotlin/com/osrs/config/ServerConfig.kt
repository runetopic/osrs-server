package com.osrs.config

import java.nio.file.Path

data class BuildConfig(
    val major: Int,
    val minor: Int,
    val token: String
)

data class CacheConfig(
    val server: Path,
    val client: Path,
)

data class GameResources(
    val players: Path,
    val npcs: Path,
    val ui: Path,
    val xteas: Path
)

data class ServerConfig(
    val local: Boolean = false,
    val benchmarking: Boolean = false,
    val build: BuildConfig,
    val cache: CacheConfig,
    val resources: GameResources
)
