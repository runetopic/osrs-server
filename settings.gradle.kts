rootProject.name = "osrs-server"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.8.22"
        kotlin("plugin.serialization") version "1.8.22"
    }
}

include(
    listOf(
        "application",
        "http-server",
        "domain",
        "repository",
        "service",
        "serializer",
        "game-server:api",
        "game-server:cache",
        "game-server:game"
    )
)
