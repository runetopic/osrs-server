package com.osrs.service.xtea

import com.google.inject.Inject
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.util.MissingResourceException
import kotlin.reflect.jvm.jvmName

@Singleton
class XteaService @Inject constructor(
    environment: ApplicationEnvironment
) {
    private val json = Json { allowStructuredMapKeys = true; ignoreUnknownKeys = true }
    private val resource = XteaService::class.java.getResourceAsStream(environment.config.property("game.resources.xteas").getString()) ?: throw MissingResourceException("Missing Xtea keys. Please set this up in the application_conf file.", XteaService::class.jvmName, "game.resources.xteas")
    @OptIn(ExperimentalSerializationApi::class)
    private val xteas: Map<Int, Xtea> = json.decodeFromStream<List<Xtea>>(resource).associateBy { it.mapsquare }

    fun find(regionId: Int): List<Int> = xteas[regionId]?.key ?: emptyList()
}
