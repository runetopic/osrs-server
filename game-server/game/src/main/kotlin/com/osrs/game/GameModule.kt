package com.osrs.game

import com.osrs.common.ui.InterfaceInfoMap
import com.osrs.game.network.packet.builder.impl.sync.PlayerUpdateBlocks
import com.osrs.game.tick.GameTick
import com.osrs.game.tick.GameTickProvider
import com.osrs.game.ui.UserInterfaceModule
import com.osrs.game.world.World
import com.osrs.game.world.WorldProvider
import com.osrs.game.world.map.CollisionMap
import com.osrs.game.world.map.path.PathFinderProvider
import com.osrs.game.world.map.path.StepValidatorProvider
import com.osrs.game.world.map.zone.ZoneManager
import com.osrs.game.world.service.LoginService
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.pathfinder.PathFinder
import org.rsmod.pathfinder.StepValidator
import org.rsmod.pathfinder.ZoneFlags

object GameModule : KotlinModule() {
    override fun configure() {
        install(UserInterfaceModule)
        bind<InterfaceInfoMap>().asEagerSingleton()
        bind<ZoneFlags>().toInstance(ZoneFlags())
        bind<ZoneManager>().toInstance(ZoneManager())
        bind<PathFinder>().toProvider<PathFinderProvider>().asEagerSingleton()
        bind<StepValidator>().toProvider<StepValidatorProvider>().asEagerSingleton()
        bind<World>().toProvider<WorldProvider>().asEagerSingleton()
        bind<CollisionMap>().asEagerSingleton()
        bind<PlayerUpdateBlocks>().asEagerSingleton()
        bind<LoginService>().asEagerSingleton()
        bind<GameTick>().toProvider<GameTickProvider>().asEagerSingleton()
    }
}
