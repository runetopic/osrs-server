package com.osrs.game

import com.osrs.game.tick.GameTick
import com.osrs.game.tick.GameTickProvider
import com.osrs.game.world.World
import com.osrs.game.world.WorldProvider
import com.osrs.game.world.map.CollisionMap
import com.osrs.game.world.map.path.PathFinderProvider
import com.osrs.game.world.map.zone.Zones
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.pathfinder.PathFinder
import org.rsmod.pathfinder.ZoneFlags

object GameModule : KotlinModule() {
    override fun configure() {
        bind<World>().toProvider<WorldProvider>().asEagerSingleton()
        bind<GameTick>().toProvider<GameTickProvider>().asEagerSingleton()
        bind<ZoneFlags>().toInstance(ZoneFlags())
        bind<Zones>().toInstance(Zones())
        bind<CollisionMap>().asEagerSingleton()
        bind<PathFinder>().toProvider<PathFinderProvider>().asEagerSingleton()
    }
}
