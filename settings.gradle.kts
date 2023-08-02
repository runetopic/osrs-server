rootProject.name = "osrs-server"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.0-RC"
        kotlin("plugin.serialization") version "1.9.0-RC"
    }
}

include(
    listOf(
        "application",
        "cache",
        "http-server",
        "domain",
        "repository",
        "service",
        "game-server:api",
        "game-server:game"
    )
)
