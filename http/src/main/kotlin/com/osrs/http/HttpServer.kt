package com.osrs.http

import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class HttpServer {
    private val javConfigFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/jav_config.ws")!!.readAllBytes()
    private val gamePackFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/gamepack.jar")!!.readAllBytes()

    fun start(): NettyApplicationEngine {
        return embeddedServer(Netty, port = 8080) {
            routing {
                get("/jav_config.ws") {
                    call.respondBytes(javConfigFile)
                }
                get("/gamepack.jar") {
                    call.respondBytes(gamePackFile)
                }
                get("/") {
                    call.respondText { "OSRS HTTP Server. y u here" }
                }
            }
        }.start(wait = false)
    }
}
