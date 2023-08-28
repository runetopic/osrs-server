package com.osrs.http.api.account

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.domain.dto.CreateAccountRequest
import com.osrs.domain.dto.CreateAccountResponse
import com.osrs.service.account.AccountService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

@Singleton
class AccountAPIRouting @Inject constructor(
    routing: Routing,
    private val accountService: AccountService
) {

    private val logger = InlineLogger()

    init {
        routing {
            post("/api/account/create") {
                val request = call.receive<CreateAccountRequest>()
                logger.info { "Creating account with request $request" }

                try {
                    accountService.createAccount(request) ?: return@post call.response.status(HttpStatusCode.Conflict)
                    call.respond(
                        status = HttpStatusCode.Created,
                        CreateAccountResponse(
                            message = "Account successfully created."
                        )
                    )
                } catch (exception: Exception) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        CreateAccountResponse(
                            message = "Account failed during creation. Please try again later."
                        )
                    )
                }
            }
        }
    }
}
