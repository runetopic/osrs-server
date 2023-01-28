package com.osrs.game.world

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.entry.map.MapSquareEntryTypeMap
import com.osrs.game.world.map.CollisionMap
import org.rsmod.pathfinder.StepValidator

@Singleton
class WorldProvider @Inject constructor(
    private val cache: Cache,
    private val maps: MapSquareEntryTypeMap,
    private val collisionMap: CollisionMap,
    private val stepValidator: StepValidator
) : Provider<World> {

    override fun get(): World = World(
        1,
        cache,
        maps,
        collisionMap,
        stepValidator
    )
}
