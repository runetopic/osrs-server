plugins {
    kotlin("jvm") version "1.6.10"
}

allprojects {
    group = "com.osrs"
    version = "1.0.0-SNAPSHOT"

    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper> {
        dependencies {
            implementation(kotlin("stdlib"))
            // Ktor
            implementation("io.ktor:ktor-server-core:2.1.3")
            implementation("io.ktor:ktor-server-netty:2.1.3")
            // Runetopic
            implementation("com.runetopic.cryptography:cryptography:1.0.10-SNAPSHOT")
            implementation("com.runetopic.cache:cache:1.6.0")
            // Logger
            implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.4")
            implementation("org.slf4j:slf4j-simple:2.0.3")
            // Kotlin-guice
            implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.6.0")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}