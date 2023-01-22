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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
        }
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        // Runetopic
        implementation("com.runetopic.cryptography:cryptography:1.0.10-SNAPSHOT")
        implementation("com.runetopic.cache:cache:1.6.0")

        // Pathfinder
        implementation("com.github.blurite:pathfinder:2.4.2")
        // Logger
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.4")
        implementation("org.slf4j:slf4j-simple:2.0.5")
        // Ktor
        implementation("io.ktor:ktor-server-core:2.2.1")
        implementation("io.ktor:ktor-server-netty:2.2.1")
        // Kotlin-guice
        implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.6.0")
        // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
        implementation("org.litote.kmongo:kmongo-id-serialization:4.8.0")
        implementation("org.litote.kmongo:kmongo-serialization:4.8.0")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.1")
        implementation("io.ktor:ktor-server-content-negotiation:2.2.1")
        implementation("org.mindrot:jbcrypt:0.4")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_19.majorVersion))
    }
}
