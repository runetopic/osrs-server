plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:common"))
    implementation(project(":game-server:database"))
    implementation(project(":game-server:game"))
}
