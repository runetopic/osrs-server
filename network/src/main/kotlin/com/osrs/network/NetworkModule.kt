package com.osrs.network

import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.network.sockets.ServerSocket

@Singleton
object NetworkModule : KotlinModule() {
    override fun configure() {
        bind<ServerSocket>().toProvider<ServerSocketProvider>().asEagerSingleton()
    }
}
