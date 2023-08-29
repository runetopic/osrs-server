package com.osrs.game

import com.osrs.api.map.MapSquares
import com.osrs.api.map.NPCConfigList
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.game.command.CommandModule
import com.osrs.game.network.packet.builder.impl.render.PlayerUpdateBlocks
import com.osrs.game.clock.GameClock
import com.osrs.game.clock.GameClockProvider
import com.osrs.game.ui.UserInterfaceModule
import com.osrs.game.world.World
import com.osrs.game.world.WorldProvider
import com.osrs.game.world.map.CollisionMap
import com.osrs.game.world.map.path.PathFinderProvider
import com.osrs.game.world.map.path.StepValidatorProvider
import com.osrs.game.world.service.LoginService
import com.osrs.game.world.service.PlayerSerializationService
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.pathfinder.PathFinder
import org.rsmod.pathfinder.StepValidator
import org.rsmod.pathfinder.ZoneFlags

object GameModule : KotlinModule() {
    override fun configure() {
        install(CommandModule)
        install(UserInterfaceModule)
        bind<MapSquares>().asEagerSingleton()
        bind<NPCConfigList>().asEagerSingleton()
        bind<InterfaceInfoMap>().asEagerSingleton()
        bind<ZoneFlags>().toInstance(ZoneFlags())
        bind<PathFinder>().toProvider<PathFinderProvider>().asEagerSingleton()
        bind<StepValidator>().toProvider<StepValidatorProvider>().asEagerSingleton()
        bind<World>().toProvider<WorldProvider>().asEagerSingleton()
        bind<CollisionMap>().asEagerSingleton()
        bind<PlayerUpdateBlocks>().asEagerSingleton()
        bind<LoginService>().asEagerSingleton()
        bind<PlayerSerializationService>().asEagerSingleton()
        bind<GameClock>().toProvider<GameClockProvider>().asEagerSingleton()
    }
}
