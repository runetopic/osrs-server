package com.osrs.api.map

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.api.resource.NPCConfig
import com.osrs.api.resource.Resource
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class NPCConfigList(
    private val spawns: List<NPCConfig> = mutableListOf()
) : List<NPCConfig> by spawns {
    @Inject
    constructor(environment: ApplicationEnvironment) : this(
        Resource.parseYamlConfigFiles<NPCConfig>(
            environment
                .config
                .property("game.configuration.npcs")
                .getString()
        )
    )
}
