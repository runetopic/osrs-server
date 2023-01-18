rootProject.name = "osrs-server"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.0"
        kotlin("plugin.serialization") version "1.7.0"
    }
}

include(
    listOf(
        "application",
        "http-server",
        "database",
        "game-server:common",
        "game-server:cache",
        "game-server:game"
    )
)
