package com.osrs.network.codec.game

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import io.ktor.server.application.ApplicationEnvironment

@Singleton
class GameCodecProvider @Inject constructor(
    private val environment: ApplicationEnvironment
) : Provider<GameCodec> {
    override fun get(): GameCodec = GameCodec(
        environment
    )
}
