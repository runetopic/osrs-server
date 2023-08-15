plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":cache"))
    implementation(project(":repository"))
    implementation(project(":service"))
    implementation(project(":http-server"))
    implementation(project(":game-server:api"))
    implementation(project(":game-server:game"))
    implementation(project(":game-server:script"))
    findContentPlugins(project(":game-server:content")).map(::implementation).let {
        project.logger.lifecycle("Included ${it.size} content plugins from: ${it}.")
    }
}

fun findContentPlugins(pluginProject: ProjectDependency): List<Project> =
    pluginProject
        .dependencyProject
        .subprojects.filter { it.buildFile.exists() }
        .map { it }
