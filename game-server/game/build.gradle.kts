plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:api"))
    implementation(project(":game-server:database"))
}
