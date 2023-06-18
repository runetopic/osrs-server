package com.osrs.common.ui

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.resource.Resource.loadYamlResource
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class InterfaceInfoMap(
    private val interfaceInfoMap: Map<String, InterfaceInfo> = mutableMapOf()
) : Map<String, InterfaceInfo> by interfaceInfoMap {
    @Inject constructor(environment: ApplicationEnvironment) : this(
        loadYamlResource<List<InterfaceInfo>>(
            environment
                .config
                .property("game.configuration.ui")
                .getString(),
            InterfaceInfo::class
        ).associateBy { it.name }
    )
}
