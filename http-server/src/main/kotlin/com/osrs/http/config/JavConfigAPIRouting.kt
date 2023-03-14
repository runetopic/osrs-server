package com.osrs.http.config

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.world.World
import io.ktor.server.application.call
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

@Singleton
class JavConfigAPIRouting @Inject constructor(
    private val routing: Routing,
    private val world: World,
) {
    private val javConfigFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/jav_config.ws")?.readAllBytes() ?: throw IllegalStateException("Missing jav_config.ws in /client_config/")
    private val gamePackFile: ByteArray = object {}::class.java.getResourceAsStream("/client_config/gamepack.jar")?.readAllBytes() ?: throw IllegalStateException("Missing gamepack.jar in /client_config/")

    init {
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
    }
}
