plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":game-server:api"))
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:database"))
    implementation(project(":game-server:game"))
}
