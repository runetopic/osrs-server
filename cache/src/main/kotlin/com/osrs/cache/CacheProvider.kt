package com.osrs.cache

import com.google.inject.Provider
import com.google.inject.name.Named
import org.openrs2.cache.Cache

class CacheProvider constructor(
    @Named("game.cache.path")
    val path: String
) : Provider<Cache> {
    override fun get(): Cache {
        TODO("Not yet implemented")
    }
}
