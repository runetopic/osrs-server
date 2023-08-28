package com.osrs.cache

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.name.Named
import com.runetopic.cache.store.Js5Store
import java.nio.file.Path

class Js5StoreProvider @Inject constructor(
    @Named("game.cache.path")
    private val path: String,
    @Named("game.cache.parallel")
    private val parallel: Boolean
) : Provider<Js5Store> {

    override fun get(): Js5Store {
        return Js5Store(Path.of(path), parallel)
    }
}
