plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:api"))
    implementation(project(":domain"))
    implementation(project(":game-server:game"))
}
