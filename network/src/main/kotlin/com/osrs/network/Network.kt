package com.osrs.network

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.Cache
import com.osrs.network.session.Session
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

private val logger = InlineLogger()

class Network @Inject constructor(private val application: Application, private val cache: Cache) {
    fun bind() = runBlocking {
        val server = aSocket(
            ActorSelectorManager(
                Executors
                    .newCachedThreadPool()
                    .asCoroutineDispatcher()
            )
        ).tcp().bind(
            hostname = "0.0.0.0",
            port = application.environment.config.property("ktor.deployment.port").getString().toInt()
        ) {
            this.backlogSize = 2000
            this.reuseAddress = true
        }

        logger.info { "Server is now accepting connections on ${server.localAddress} and listening for incoming connections." }
        handleClientSessions(server, application.environment)
    }

    private suspend fun handleClientSessions(
        server: ServerSocket,
        environment: ApplicationEnvironment
    ) = coroutineScope {
        val scope = CoroutineScope(
            coroutineContext + SupervisorJob() + CoroutineExceptionHandler { ctx, throwable ->
                logger.error { "${throwable.message} $ctx" }
            }
        )

        with(scope) {
            while (this.isActive) {
                val session = Session(server.accept(), environment, cache)
                launch(Dispatchers.IO) { session.connect() }
            }
        }
    }
}
