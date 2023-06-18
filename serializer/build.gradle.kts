plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:api"))
}
