rootProject.name = "osrs-server"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.21"
        kotlin("plugin.serialization") version "1.7.21"
    }
}

include(
    listOf(
        "application",
        "http-server",
        "game-server:api",
        "game-server:cache",
        "game-server:game",
        "game-server:database"
    )
)
