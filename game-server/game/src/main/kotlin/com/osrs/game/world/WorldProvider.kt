package com.osrs.game.world

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.entry.config.map.MapSquareEntryProvider
import com.osrs.game.world.map.CollisionMap

@Singleton
class WorldProvider @Inject constructor(
    private val cache: Cache,
    private val maps: MapSquareEntryProvider,
    private val collisionMap: CollisionMap
) : Provider<World> {

    override fun get(): World = World(
        1,
        cache,
        maps,
        collisionMap
    )
}
