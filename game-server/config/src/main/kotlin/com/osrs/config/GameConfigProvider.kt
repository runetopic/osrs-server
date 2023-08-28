package com.osrs.config

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.config.ApplicationConfig
import kotlin.reflect.KClass

class GameConfigProvider @Inject constructor(
    val config: ApplicationConfig
) : Provider<GameConfig> {
    override fun get(): GameConfig = mapToObject(config.toMap(), GameConfig::class)

    private fun <T : Any> mapToObject(map: Map<String, Any?>, clazz: KClass<T>) : T {
        val constructor = clazz.constructors.first()

        val args = constructor
            .parameters.associateWith { map[it.name] }

        return constructor.callBy(args)
    }
}
