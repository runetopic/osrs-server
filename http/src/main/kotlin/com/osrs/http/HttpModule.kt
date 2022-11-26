package com.osrs.http

import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.*

object HttpModule : KotlinModule() {
    override fun configure() {
        bind<NettyApplicationEngine>().toProvider<NettyApplicationProvider>().asEagerSingleton()
        bind<Routing>().toProvider<HttpRoutingProvider>().asEagerSingleton()
    }
}
