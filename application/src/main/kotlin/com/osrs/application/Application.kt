package com.osrs.application

import com.google.inject.Guice
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule
import com.osrs.database.DatabaseModule
import com.osrs.game.Game
import com.osrs.game.GameModule
import com.osrs.game.network.Network
import com.osrs.game.network.NetworkModule
import com.osrs.game.world.World
import com.osrs.http.HttpModule
import dev.misfitlabs.kotlinguice4.getInstance

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        val injector = Guice.createInjector(
            ApplicationModule(args),
            CacheModule,
            DatabaseModule,
            HttpModule,
            GameModule,
            NetworkModule
        )
        injector.getInstance<Cache>().load()
        injector.getInstance<Game>().start()
        injector.getInstance<World>().start()
        injector.getInstance<Network>().bind()
    }
}
