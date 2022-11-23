package com.osrs.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.runetopic.cache.store.Js5Store
import io.ktor.server.application.Application
import java.nio.file.Path

class CacheProvider @Inject constructor(private val application: Application) : Provider<Cache> {
    override fun get(): Cache {
        val path = application.environment.config.property("game.cache.path").getString()
        val parallel = application.environment.config.property("game.cache.parallel").getString().toBoolean()
        val store = Js5Store(Path.of(path), parallel, intArrayOf(5))
        return Cache(store)
    }
}
