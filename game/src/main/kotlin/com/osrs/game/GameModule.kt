package com.osrs.game

import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.application.Application

@Singleton
class GameModule(private val application: Application) : KotlinModule() {
    override fun configure() {
        bind<Application>().toInstance(application)
    }
}
