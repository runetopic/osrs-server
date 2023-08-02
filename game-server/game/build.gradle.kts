plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":game-server:api"))
    implementation(project(":domain"))
    implementation(project(":service"))
}
