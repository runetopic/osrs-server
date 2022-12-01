plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:game"))
    implementation(project(":database"))
}
