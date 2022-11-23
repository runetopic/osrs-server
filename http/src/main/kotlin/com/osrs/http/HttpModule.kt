package com.osrs.http

import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.netty.NettyApplicationEngine

object HttpModule : KotlinModule() {
    override fun configure() {
        bind<NettyApplicationEngine>().toProvider<NettyApplicationProvider>().`in`<Singleton>()
    }
}

