package com.osrs.common.ui

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class InterfaceInfoMap(
    private val interfaceInfoMap: Map<String, InterfaceInfo> = mutableMapOf()
) : Map<String, InterfaceInfo> by interfaceInfoMap {
    @Inject constructor(environment: ApplicationEnvironment) : this(
        Yaml.default.decodeFromStream<List<InterfaceInfo>>(InterfaceInfo::class.java.getResourceAsStream(environment.config.property("game.configuration.ui").getString())!!).toList().associateBy { it.name })
}
