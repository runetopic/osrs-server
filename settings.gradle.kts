import java.nio.file.Files
import java.nio.file.Path

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
        "game-server:game",
        "game-server:config",
        "game-server:content",
        "game-server:script",
    )
)

includePlugins(project(":game-server:content").projectDir.toPath())

fun includePlugins(pluginPath: Path) {
    try {
        Files.walk(pluginPath).forEach {
            if (!Files.isDirectory(it)) {
                return@forEach
            }
            searchPlugin(pluginPath, it)
        }
    } catch (e: java.io.IOException) {
        System.err.println("Failed to walk plugin dir, skipping")
    }
}

fun searchPlugin(parent: Path, path: Path) {
    val hasBuildFile = Files.exists(path.resolve("build.gradle.kts"))
    if (!hasBuildFile) {
        return
    }
    val relativePath = parent.relativize(path)
    val pluginName = relativePath.toString().replace(File.separator, ":")
    include(":game-server:content:$pluginName")
}
