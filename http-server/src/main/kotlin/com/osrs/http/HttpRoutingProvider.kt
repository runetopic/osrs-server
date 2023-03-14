package com.osrs.http

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

class HttpRoutingProvider @Inject constructor(
    private val nettyApplicationEngine: NettyApplicationEngine
) : Provider<Routing> {
    override fun get(): Routing = nettyApplicationEngine.application.routing {}
}
