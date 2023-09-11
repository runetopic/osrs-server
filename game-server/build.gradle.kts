plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":cache-osrs"))
    implementation(project(":domain"))
    implementation(project(":game-server:api"))
    implementation(project(":game-server:game"))
}
