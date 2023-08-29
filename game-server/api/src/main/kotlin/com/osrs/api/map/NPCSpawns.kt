package com.osrs.api.map

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.resource.Resource
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class NPCSpawns(
    private val spawns: List<NPCSpawnsResource> = mutableListOf()
) : List<NPCSpawnsResource> by spawns {
    @Inject
    constructor(environment: ApplicationEnvironment) : this(
        Resource.loadYamlResource<List<NPCSpawnsResource>>(
            environment
                .config
                .property("game.configuration.spawns")
                .getString(),
            NPCSpawnsResource::class
        )
    )
}
