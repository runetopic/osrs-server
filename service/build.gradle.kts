plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":cache-osrs"))
    implementation(project(":game-server:api"))
}
