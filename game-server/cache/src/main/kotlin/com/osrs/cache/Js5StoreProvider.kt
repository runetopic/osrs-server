package com.osrs.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.runetopic.cache.store.Js5Store
import io.ktor.server.application.Application
import java.nio.file.Path

class Js5StoreProvider @Inject constructor(
    private val application: Application
) : Provider<Js5Store> {

    override fun get(): Js5Store {
        val path = application.environment.config.property("game.cache.path").getString()
        val parallel = application.environment.config.property("game.cache.parallel").getString().toBoolean()
        return Js5Store(Path.of(path), parallel, intArrayOf(5))
    }
}
