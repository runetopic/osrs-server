package com.osrs.game

import com.osrs.game.tick.GameTick
import com.osrs.game.tick.GameTickProvider
import com.osrs.game.world.World
import com.osrs.game.world.WorldProvider
import dev.misfitlabs.kotlinguice4.KotlinModule

object GameModule : KotlinModule() {
    override fun configure() {
        bind<World>().toProvider<WorldProvider>().asEagerSingleton()
        bind<GameTick>().toProvider<GameTickProvider>().asEagerSingleton()
    }
}
