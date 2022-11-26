package com.osrs.http

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.netty.*
import io.ktor.server.routing.*

class HttpRoutingProvider @Inject constructor(
    private val nettyApplicationEngine: NettyApplicationEngine
) : Provider<Routing> {
    override fun get(): Routing = nettyApplicationEngine.application.routing {}
}
