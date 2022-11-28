package com.osrs.application

import com.google.inject.Guice
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule
import com.osrs.game.Game
import com.osrs.game.GameModule
import com.osrs.http.HttpModule
import com.osrs.http.HttpRouting
import com.osrs.network.Network
import com.osrs.network.NetworkModule
import com.osrs.service.ServiceModule
import dev.misfitlabs.kotlinguice4.getInstance

fun main(args: Array<String>) {
    val injector = Guice.createInjector(
        ApplicationModule(args),
        HttpModule,
        ServiceModule,
        GameModule,
        CacheModule,
        NetworkModule
    )
    injector.getInstance<Cache>().load()
    injector.getInstance<Game>().start()
    injector.getInstance<HttpRouting>().bind()
    injector.getInstance<Network>().bind()
}
