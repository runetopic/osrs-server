package com.osrs.game.network

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

class ServerSocketProvider @Inject constructor(
    private val environment: ApplicationEnvironment
) : Provider<ServerSocket> {
    override fun get(): ServerSocket = aSocket(
        ActorSelectorManager(
            Executors
                .newCachedThreadPool()
                .asCoroutineDispatcher()
        )
    ).tcp().bind(
        hostname = "0.0.0.0",
        port = environment.config.property("ktor.deployment.port").getString().toInt()
    ) {
        this.backlogSize = 2000
        this.reuseAddress = true
    }
}
