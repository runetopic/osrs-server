plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":cache"))
    implementation(project(":game-server:api"))
}
