plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":cache-osrs"))
    implementation(project(":game-server:api"))
}
