package com.osrs.config

data class BuildConfig(
    val major: Int,
    val minor: Int,
    val token: String
)

data class CacheConfig(
    val path: String,
    val parallel: Boolean
)

data class GameConfig(
    val local: Boolean,
    val benchmarking: Boolean,
    val build: BuildConfig,
    val cache: CacheConfig
)

data class GameConfiguration(
    val game: GameConfig,
)
