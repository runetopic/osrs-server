package com.osrs.common.map

import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
@Singleton
class MapSquares(
    environment: ApplicationEnvironment,
    private val json: Json,
    private val squares: Map<Int, MapSquare> = json.decodeFromStream<List<MapSquare>>(MapSquares::class.java.getResourceAsStream(environment.config.property("game.map.xteas").getString())!!).toList().associateBy { it.id }
) : Map<Int, MapSquare> by squares {
    @Inject constructor(environment: ApplicationEnvironment) : this(environment, Json { allowStructuredMapKeys = true; ignoreUnknownKeys = true })

    fun toList(): List<MapSquare> = values.toList()
}
