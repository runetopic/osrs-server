package com.osrs.network.codec.login

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class LoginCodecProvider @Inject constructor(
    private val cache: Cache,
    private val environment: ApplicationEnvironment
) : Provider<LoginCodec> {

    override fun get(): LoginCodec {
        return LoginCodec(
            cache,
            environment
        )
    }
}
