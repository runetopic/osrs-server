plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.osrs.application.Application")
    applicationDefaultJvmArgs = listOf("-XX:+UseZGC")
    tasks.run.get().workingDir = rootProject.projectDir
}

dependencies {
    implementation(project(":game-server:cache"))
    implementation(project(":game-server:common"))
    implementation(project(":database"))
    implementation(project(":game-server:game"))
    implementation(project(":http-server"))
}
