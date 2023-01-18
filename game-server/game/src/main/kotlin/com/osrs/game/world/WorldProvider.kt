package com.osrs.game.world

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache

@Singleton
class WorldProvider @Inject constructor(
    private val cache: Cache
) : Provider<World> {
    // TODO add support for multiple worlds but for now this will work
    override fun get(): World = World(1, cache)
}
