package com.osrs.application

import com.google.inject.name.Names
import com.osrs.cache.CacheModule
import com.osrs.config.ConfigModule
import com.osrs.game.GameModule
import com.osrs.game.network.NetworkModule
import com.osrs.http.HttpModule
import com.osrs.repository.RepositoryModule
import com.osrs.service.ServiceModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig
import org.openrs2.buffer.BufferModule

class ApplicationModule(
    private val args: Array<String>
) : KotlinModule() {
    override fun configure() {
        bind<Array<String>>().annotatedWith(Names.named("args")).toInstance(args)
        bind<Application>().toProvider<ApplicationProvider>().asEagerSingleton()
        bind<ApplicationEnvironment>().toProvider<ApplicationEnvironmentProvider>().asEagerSingleton()
        bind<ApplicationConfig>().toProvider<ApplicationConfigProvider>().asEagerSingleton()
        install(BufferModule)
        install(ConfigModule)
        install(CacheModule)
        install(RepositoryModule)
        install(ServiceModule)
        install(HttpModule)
        install(GameModule)
        install(NetworkModule)
    }
}
