rootProject.name = "osrs-server"

dependencyResolutionManagement {
    repositories(RepositoryHandler::mavenCentral)
    repositories { maven { url = uri("https://jitpack.io") } }
    repositories { maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") } }
}

include(listOf("cache", "http", "network", "game"))
