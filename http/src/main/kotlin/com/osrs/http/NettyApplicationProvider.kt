package com.osrs.http

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

class NettyApplicationProvider @Inject constructor(private val application: Application) : Provider<NettyApplicationEngine> {
    override fun get(): NettyApplicationEngine = embeddedServer(Netty, port = 8080) {
        application
    }.start(wait = false)
}
