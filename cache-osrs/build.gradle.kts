plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:config"))
    implementation(project(":game-server:api"))
}
