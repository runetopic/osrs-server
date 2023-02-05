package com.osrs.common.map

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.resource.Resource
import com.osrs.common.resource.Resource.loadJsonResource
import com.osrs.common.ui.InterfaceInfo
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Singleton
class MapSquares(
    private val squares: Map<Int, MapSquare> = mutableMapOf()
) : Map<Int, MapSquare> by squares {
    @Inject constructor(environment: ApplicationEnvironment) : this(
        loadJsonResource<List<MapSquare>>(
            environment
                .config
                .property("game.configuration.xteas")
                .getString(),
            MapSquare::class
        ).associateBy { it.id }
    )
}
