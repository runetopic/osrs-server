package com.osrs.network

import com.google.inject.Singleton
import com.osrs.network.codec.CodecModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.network.sockets.ServerSocket

@Singleton
object NetworkModule : KotlinModule() {
    override fun configure() {
        install(CodecModule)
        bind<ServerSocket>().toProvider<ServerSocketProvider>().asEagerSingleton()
    }
}
