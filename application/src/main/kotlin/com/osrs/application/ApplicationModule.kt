package com.osrs.application

import com.google.inject.Singleton
import com.google.inject.name.Names
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.application.Application

class ApplicationModule(private val args: Array<String>) : KotlinModule() {
    override fun configure() {
        bind<Array<String>>().annotatedWith(Names.named("args")).toInstance(args)
        bind<Application>().toProvider<ApplicationProvider>().`in`<Singleton>()
    }
}
