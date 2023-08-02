plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":game-server:api"))
    implementation(project(":domain"))
    implementation(project(":game-server:game"))
}
