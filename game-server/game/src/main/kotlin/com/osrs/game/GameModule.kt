package com.osrs.game

import com.osrs.game.tick.GameTick
import com.osrs.game.tick.GameTickProvider
import com.osrs.game.world.World
import com.osrs.game.world.WorldProvider
import com.osrs.game.world.map.zone.ZoneProvider
import com.osrs.game.world.map.zone.Zones
import dev.misfitlabs.kotlinguice4.KotlinModule

object GameModule : KotlinModule() {
    override fun configure() {
//        bind<CollisionFlagMap>().toProvider<CollisionFlagMapProvider>().asEagerSingleton()
//        bind<CollisionMap>().asEagerSingleton()
        bind<World>().toProvider<WorldProvider>().asEagerSingleton()
        bind<GameTick>().toProvider<GameTickProvider>().asEagerSingleton()
        bind<Zones>().toProvider<ZoneProvider>().asEagerSingleton()
    }
}
