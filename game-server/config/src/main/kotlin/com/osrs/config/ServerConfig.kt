package com.osrs.config

import java.nio.file.Path

data class BuildConfig(
    val major: Int,
    val minor: Int,
    val token: String
)

data class CacheConfig(
    val vanilla: Path,
    val game: Path,
    val js5: Path,
)

data class GameConfig(
    val local: Boolean = false,
    val benchmarking: Boolean = false,
    val build: BuildConfig,
    val cache: CacheConfig,
)
