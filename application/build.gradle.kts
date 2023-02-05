plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:common"))
    implementation(project(":database"))
    implementation(project(":game-server:game"))
    implementation(project(":http-server"))
}
