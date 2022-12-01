package com.osrs.http.api.xteas

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.database.xtea.XteaService
import com.osrs.http.api.xteas.response.XteasInfoResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

@Singleton
class XteasAPIRouting @Inject constructor(
    routing: Routing,
    private val xteaService: XteaService
) {
    init {
        routing {
            get("/api/xteas/{build}/{region?}") {
                val build = call.parameters["build"] ?: return@get run {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond("Please provide a build")
                }

                val region = call.parameters["region"]?.toInt() ?: 0

                if (region > 0) {
                    val xtea = xteaService.findOrNull(region) ?: return@get call.respond("Missing keys for $region")

                    call.respond(
                        XteasInfoResponse(
                            build = build.toInt(),
                            xteas = listOf(xtea)
                        )
                    )
                } else {
                    call.respond(
                        XteasInfoResponse(
                            build = build.toInt(),
                            xteas = xteaService.toList()
                        )
                    )
                }
            }
        }
    }
}
