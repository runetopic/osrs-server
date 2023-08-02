plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":game-server:api"))
}
