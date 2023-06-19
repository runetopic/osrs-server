package com.osrs.api.resource

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import kotlin.reflect.KClass

object Resource {
    val json = Json { allowStructuredMapKeys = true; ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T : Any> loadJsonResource(name: String, type: KClass<*>): T = type::class.java.getResourceAsStream(name)?.let { json.decodeFromStream<T>(it) } ?: throw IllegalStateException("Failed to load resource with name $name and type $type")
    inline fun <reified T : Any> loadYamlResource(name: String, type: KClass<*>): T = type::class.java.getResourceAsStream(name)?.let { Yaml.default.decodeFromStream<T>(it) } ?: throw IllegalStateException("Failed to load resource with name $name and type $type")

    inline fun <reified T> getResourcesDirectory(name: String): File {
        val resourceUrl = T::class.java.getResource(name) ?: throw IllegalStateException("Resource folder $name not found")

        val resourcesDirectory = File(resourceUrl.file)
        if (!resourcesDirectory.isDirectory) {
            throw IllegalStateException("Resource path $name does not point to a directory")
        }

        return resourcesDirectory
    }

    inline fun <reified T> parseYamlConfigFiles(name: String): List<T> {
        val directory = getResourcesDirectory<T>(name)

        val yaml = Yaml.default
        val configs = mutableListOf<T>()

        val npcFiles = directory.walkTopDown().filter { it.isFile && it.extension == "yaml" }

        for (file in npcFiles) {
            val yamlString = file.inputStream()
            val npcConfig = yaml.decodeFromStream<T>(yamlString)
            configs.add(npcConfig)
        }

        return configs
    }
}
