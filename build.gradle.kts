plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

allprojects {
    group = "com.osrs"
    version = "1.0.0-SNAPSHOT"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
        }
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        // Kotlin standard-lib
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0-RC")
        // Runetopic
        implementation("com.runetopic.cryptography:cryptography:1.2.0-SNAPSHOT")
        implementation("com.runetopic.cache:cache:2.0.0-SNAPSHOT")
        // Blurite Pathfinder - TODO Deprecated
        implementation("com.github.blurite:pathfinder:2.4.3")
        // Logger
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.5")
        implementation("org.slf4j:slf4j-simple:2.0.5")
        // Ktor
        // https://mvnrepository.com/artifact/io.ktor/ktor-server-core
        runtimeOnly("io.ktor:ktor-server-core:2.3.1")
        // https://mvnrepository.com/artifact/io.ktor/ktor-server-netty
        implementation("io.ktor:ktor-server-netty:2.3.1")
        // Kotlin-guice
        implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:3.0.0")
        // Kotlin-guice assisted inject https://mvnrepository.com/artifact/com.google.inject.extensions/guice-assistedinject
        implementation("com.google.inject.extensions:guice-assistedinject:5.1.0")
        // Serialization: https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
        implementation("org.litote.kmongo:kmongo-id-serialization:4.8.0")
        implementation("org.litote.kmongo:kmongo-serialization:4.8.0")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.1")
        implementation("io.ktor:ktor-server-content-negotiation:2.2.1")
        // Bcrypt
        implementation("org.mindrot:jbcrypt:0.4")
        // Kaml - Yaml parser.
        implementation("com.charleskorn.kaml:kaml:0.54.0")
        // https://mvnrepository.com/artifact/io.github.classgraph/classgraph
        implementation("io.github.classgraph:classgraph:4.8.157")
        testImplementation("org.jetbrains.kotlin:kotlin-test")
    }
}
