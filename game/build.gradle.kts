plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.osrs.game.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-XX:+UseZGC")
    tasks.run.get().workingDir = rootProject.projectDir
}

dependencies {
    implementation(project(":http"))
    implementation(project(":cache"))
    implementation(project(":network"))
}
