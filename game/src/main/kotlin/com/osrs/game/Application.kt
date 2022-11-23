package com.osrs.game

import com.google.inject.Guice
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule
import com.osrs.http.HttpServer
import com.osrs.network.Network
import com.osrs.network.NetworkModule
import dev.misfitlabs.kotlinguice4.getInstance
import io.ktor.server.application.Application
import io.ktor.server.engine.commandLineEnvironment

fun main(args: Array<String>) = commandLineEnvironment(args).start()

fun Application.module() {
    val injector = Guice.createInjector(
        GameModule(this),
        CacheModule,
        NetworkModule
    )
    injector.getInstance<HttpServer>().start()
    injector.getInstance<Cache>().load()
    injector.getInstance<Network>().bind()
}
