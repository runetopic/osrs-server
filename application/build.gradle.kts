plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":repository"))
    implementation(project(":service"))
    implementation(project(":http-server"))
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:api"))
    implementation(project(":game-server:game"))
}
