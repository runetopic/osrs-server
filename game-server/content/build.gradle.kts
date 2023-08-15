plugins {
    kotlin("jvm")
}

allprojects {
    dependencies {
        implementation(project(":cache"))
        implementation(project(":game-server:api"))
        implementation(project(":game-server:game"))
        implementation(project(":game-server:script"))
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.9.0")
    }
}
