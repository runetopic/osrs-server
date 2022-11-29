plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.osrs.application.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-XX:+UseZGC")
    tasks.run.get().workingDir = rootProject.projectDir
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":game"))
    implementation(project(":http"))
}
