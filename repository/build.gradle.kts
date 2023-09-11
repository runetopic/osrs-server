plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":cache-osrs"))
    implementation(project(":game-server:api"))
    implementation(project(":game-server:config"))
}
