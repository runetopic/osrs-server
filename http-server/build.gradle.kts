plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:game"))
    implementation(project(":domain"))
    implementation(project(":service"))
}
