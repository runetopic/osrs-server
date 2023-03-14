package com.osrs.application

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import com.osrs.game.Game
import com.osrs.game.network.Network
import com.osrs.game.world.World
import dev.misfitlabs.kotlinguice4.getInstance
import kotlin.system.measureTimeMillis

object Application {
    private val logger = InlineLogger()

    @JvmStatic
    fun main(args: Array<String>) {
        var injector: Injector

        val time = measureTimeMillis {
            injector = Guice.createInjector(ApplicationModule(args))
            injector.getInstance<Game>().start()
            injector.getInstance<World>().start()
        }

        addShutDownHook(injector)

        injector.getInstance<Network>().bind(time)
    }

    private fun addShutDownHook(injector: Injector) {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info { "Gracefully shutting down the server." }
                injector.getInstance<Game>().shutdown()
                injector.getInstance<Network>().shutdown()
            }
        )
    }
}
