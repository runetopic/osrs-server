package com.osrs.application

import com.google.inject.name.Names
import com.osrs.api.map.MapSquares
import com.osrs.api.map.NPCConfigList
import com.osrs.cache.CacheModule
<<<<<<< HEAD
=======
import com.osrs.api.map.MapSquares
import com.osrs.api.map.NPCSpawns
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
import com.osrs.game.GameModule
import com.osrs.game.network.NetworkModule
import com.osrs.http.HttpModule
import com.osrs.repository.RepositoryModule
import com.osrs.service.ServiceModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment

class ApplicationModule(
    private val args: Array<String>
) : KotlinModule() {
    override fun configure() {
        bind<Array<String>>().annotatedWith(Names.named("args")).toInstance(args)
        bind<Application>().toProvider<ApplicationProvider>().asEagerSingleton()
        bind<ApplicationEnvironment>().toProvider<ApplicationEnvironmentProvider>().asEagerSingleton()
        bind<MapSquares>().asEagerSingleton()
<<<<<<< HEAD
        bind<NPCConfigList>().asEagerSingleton()
=======
        bind<NPCSpawns>().asEagerSingleton()
>>>>>>> af9ba4d13b573c0ef331120b746d09ecb601c0d1
        install(CacheModule)
        install(RepositoryModule)
        install(ServiceModule)
        install(HttpModule)
        install(GameModule)
        install(NetworkModule)
    }
}
