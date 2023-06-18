package com.osrs.common.resource

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.reflect.KClass

object Resource {
    val json = Json { allowStructuredMapKeys = true; ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T : Any> loadJsonResource(name: String, type: KClass<*>): T = type::class.java.getResourceAsStream(name)?.let { json.decodeFromStream<T>(it) } ?: throw IllegalStateException("Failed to load resource with name $name and type $type")
    inline fun <reified T : Any> loadYamlResource(name: String, type: KClass<*>): T = type::class.java.getResourceAsStream(name)?.let { Yaml.default.decodeFromStream<T>(it) } ?: throw IllegalStateException("Failed to load resource with name $name and type $type")
}
