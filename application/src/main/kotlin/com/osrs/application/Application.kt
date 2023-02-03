package com.osrs.application

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.osrs.cache.Cache
import com.osrs.game.Game
import com.osrs.game.network.Network
import com.osrs.game.world.World
import dev.misfitlabs.kotlinguice4.getInstance
import kotlin.system.measureTimeMillis

object Application {
    private val logger = InlineLogger()

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val injector = Guice.createInjector(ApplicationModule(args))

            val time = measureTimeMillis {
                injector.getInstance<Cache>().load()
                injector.getInstance<Game>().start()
                injector.getInstance<World>().start()
            }

            logger.info { "Application took $time to fully spin up." }
            injector.getInstance<Network>().bind()
        } catch (exception: Exception) {
            logger.error(exception) { "There was an error starting up the server: "}
        }
    }
}
