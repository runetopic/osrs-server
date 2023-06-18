plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:api"))
}
