package com.osrs.network

import com.google.inject.Singleton
import com.osrs.network.codec.CodecModule
import com.osrs.network.packet.PacketModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.network.sockets.ServerSocket

@Singleton
object NetworkModule : KotlinModule() {
    override fun configure() {
        install(CodecModule)
        install(PacketModule)
        bind<ServerSocket>().toProvider<ServerSocketProvider>().asEagerSingleton()
    }
}
