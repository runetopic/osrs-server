package com.osrs.common.resource

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import kotlin.reflect.KClass

object Resource {
    inline fun <reified T : Any> loadResource(name: String, type: KClass<*>): T = type::class.java.getResourceAsStream(name)?.let { Yaml.default.decodeFromStream<T>(it) } ?: throw IllegalStateException("Failed to load resource with name $name and type $type")
}
