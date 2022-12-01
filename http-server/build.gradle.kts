plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:game"))
    implementation(project(":game-server:database"))
}
