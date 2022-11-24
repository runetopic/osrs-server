package com.osrs.http

import com.google.inject.Provider
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

class NettyApplicationProvider : Provider<NettyApplicationEngine> {
    override fun get(): NettyApplicationEngine = embeddedServer(Netty, port = 8080) {}.start(wait = false)
}
