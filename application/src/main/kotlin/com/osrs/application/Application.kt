package com.osrs.application

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import com.osrs.cache.config.enum.EnumType
import com.osrs.cache.config.enum.EnumTypeList
import com.osrs.game.Game
import com.osrs.game.network.Network
import com.osrs.game.world.World
import com.osrs.script.Scripts
import com.osrs.script.content.ContentScript
import dev.misfitlabs.kotlinguice4.getInstance
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
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

        loadContentScripts(injector).let {
            logger.info { "Loaded ${it.size} kotlin script${if (it.size == 1) "" else "s"}." }
        }

        val enums = injector.getInstance<EnumTypeList>()


        println("String: "+ enums[823]?.getString(0))

        addShutDownHook(injector)
        System.gc()
        injector.getInstance<Network>().bind(time)
    }

    private fun loadContentScripts(injector: Injector): List<ContentScript> = Scripts.loadContentScripts(injector)

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


