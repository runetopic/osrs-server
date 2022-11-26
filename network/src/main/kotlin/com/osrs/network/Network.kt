package com.osrs.network

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.cache.Cache
import io.ktor.network.sockets.ServerSocket
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Network @Inject constructor(
    private val cache: Cache,
    private val server: ServerSocket,
    private val environment: ApplicationEnvironment,
) {
    private val logger = InlineLogger()

    private val scope = CoroutineScope(
        coroutineContext + SupervisorJob() + CoroutineExceptionHandler { ctx, throwable ->
            logger.error { "${throwable.message} $ctx" }
        }
    )

    fun bind() = runBlocking {
        logger.info { "Server is now accepting connections on ${server.localAddress} and listening for incoming connections." }
        with(scope) {
            while (this.isActive) {
                val session = Session(server.accept(), cache, environment)
                launch(Dispatchers.IO) { session.connect() }
            }
        }
    }
}
