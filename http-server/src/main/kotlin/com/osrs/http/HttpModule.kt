package com.osrs.http

import com.osrs.http.api.account.AccountAPIRouting
import com.osrs.http.api.world.WorldAPIRouting
import com.osrs.http.config.JavConfigAPIRouting
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.Routing

object HttpModule : KotlinModule() {
    override fun configure() {
        bind<NettyApplicationEngine>().toProvider<NettyApplicationProvider>().asEagerSingleton()
        bind<Routing>().toProvider<HttpRoutingProvider>().asEagerSingleton()
        bind<JavConfigAPIRouting>().asEagerSingleton()
        bind<WorldAPIRouting>().asEagerSingleton()
        bind<AccountAPIRouting>().asEagerSingleton()
    }
}
