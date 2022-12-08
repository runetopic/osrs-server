package com.osrs.database.xtea

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Singleton
class XteaService @Inject constructor(
    private val environment: ApplicationEnvironment
) {
    private val json = Json { allowStructuredMapKeys = true; ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val xteas = json.decodeFromStream<List<Xtea>>(XteaService::class.java.getResourceAsStream(environment.config.property("game.map.xteas").getString())!!).toList().associateBy { it.mapSquare }

    private val logger = InlineLogger()

    init {
        logger.info { "Finished loading ${xteas.size} map squares." }
    }

    fun toList(): List<Xtea> = xteas.values.toList()
    fun findOrNull(regionId: Int): Xtea? = xteas[regionId]
    fun find(regionId: Int): List<Int> = xteas[regionId]?.key ?: throw IllegalStateException("Missing keys for $regionId")
}
