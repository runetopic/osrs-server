package com.osrs.game.world

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache
//import com.osrs.cache.entry.map.MapSquareTypeProvider
import com.osrs.game.world.map.CollisionMap
import com.osrs.game.world.service.LoginService
import com.osrs.game.world.service.PlayerSerializationService
import org.rsmod.pathfinder.StepValidator

@Singleton
class WorldProvider @Inject constructor(
    private val cache: Cache,
    private val loginService: LoginService,
    private val playerSerializationService: PlayerSerializationService,
//    private val maps: MapSquareTypeProvider,
    private val collisionMap: CollisionMap,
    private val stepValidator: StepValidator
) : Provider<World> {

    private val world = World(
        1,
        cache,
        loginService,
        playerSerializationService,
//        maps,
        collisionMap,
        stepValidator
    )

    override fun get(): World = world
}
