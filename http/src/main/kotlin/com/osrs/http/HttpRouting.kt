package com.osrs.http

import com.google.inject.Inject
import io.ktor.server.application.call
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class HttpRouting @Inject constructor(private val applicationEngine: NettyApplicationEngine) {
    private val javConfigFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/jav_config.ws")?.readAllBytes() ?: throw IllegalStateException("Missing jav_config.ws in /client_config/")
    private val gamePackFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/gamepack.jar")?.readAllBytes() ?: throw IllegalStateException("Missing gamepack.jar in /client_config/")

    fun bind(): Routing = applicationEngine.application.routing {
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
}
