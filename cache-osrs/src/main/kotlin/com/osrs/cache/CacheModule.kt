package com.osrs.cache

import com.osrs.cache.client.ClientCache
import com.osrs.cache.client.ClientCacheModule
import com.osrs.cache.pack.obj.ObjPack
import com.osrs.cache.server.ServerCache
import com.osrs.cache.server.ServerCacheModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Cache

object CacheModule : KotlinModule() {

    override fun configure() {
        bind<Cache>().annotatedWith<ServerCache>().toProvider<ClientCacheProvider>().asEagerSingleton()
        bind<Cache>().annotatedWith<ClientCache>().toProvider<ClientCacheProvider>().asEagerSingleton()
        bind<ObjPack>().asEagerSingleton()
        install(ServerCacheModule)
        install(ClientCacheModule)
    }
}
