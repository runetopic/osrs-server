package com.osrs.application

import com.google.inject.name.Names
import com.osrs.cache.CacheModule
import com.osrs.common.map.MapSquares
import com.osrs.database.DatabaseModule
import com.osrs.game.GameModule
import com.osrs.game.network.NetworkModule
import com.osrs.http.HttpModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class ApplicationModule(
    private val args: Array<String>
) : KotlinModule() {
    override fun configure() {
        bind<Array<String>>().annotatedWith(Names.named("args")).toInstance(args)
        bind<Application>().toProvider<ApplicationProvider>().asEagerSingleton()
        bind<ApplicationEnvironment>().toProvider<ApplicationEnvironmentProvider>().asEagerSingleton()
        bind<MapSquares>().asEagerSingleton()
        install(CacheModule)
        install(DatabaseModule)
        install(HttpModule)
        install(GameModule)
        install(NetworkModule)
    }
}
