package com.osrs.http

import com.google.inject.Provider
import com.google.inject.Singleton
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

@Singleton
class NettyApplicationProvider : Provider<NettyApplicationEngine> {
    override fun get(): NettyApplicationEngine = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(
                Json {
                    serializersModule = IdKotlinXSerializationModule
                    prettyPrint = true
                }
            )
        }
    }.start(wait = false)
}
