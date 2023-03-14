package com.osrs.http.api.world

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.game.world.World
import com.osrs.http.api.world.response.WorldInfoData
import com.osrs.http.api.world.response.WorldInfoResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

@Singleton
class WorldAPIRouting @Inject constructor(
    routing: Routing,
    private val world: World
) {
    init {
        routing {
            get("/api/world/{worldId}") {
                val worldId = call.parameters["worldId"] ?: run {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@get call.respond(
                        WorldInfoResponse(
                            message = "Please provide a world id. ${call.request.uri}/{worldId}"
                        )
                    )
                }

                if (world.worldId != worldId.toInt()) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@get call.respond(
                        WorldInfoResponse(
                            message = "World with worldId $worldId does not exists."
                        )
                    )
                }

                return@get call.respond(
                    WorldInfoResponse(
                        message = "World is healthy and online.",
                        data = WorldInfoData(
                            players = world.players.size
                        )
                    )
                )
            }
        }
    }
}
