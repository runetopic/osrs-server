package com.osrs.network.codec.handshake

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class HandshakeCodecProvider @Inject constructor(
    val environment: ApplicationEnvironment

) : Provider<HandshakeCodec> {
    override fun get(): HandshakeCodec {
        return HandshakeCodec(
            environment
        )
    }
}
