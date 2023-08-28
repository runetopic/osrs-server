package com.osrs.config

data class BuildConfig(
    val major: Int,
    val minor: Int,
    val token: String
)

data class GameConfig(
    val local: Boolean,
    val benchmarking: Boolean,
    val build: BuildConfig,
)
