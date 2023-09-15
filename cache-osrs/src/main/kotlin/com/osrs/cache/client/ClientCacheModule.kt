package com.osrs.cache.client

import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store

object ClientCacheModule : KotlinModule() {

    override fun configure() {
        bind<Store>().annotatedWith<ClientCache>().toProvider<ClientStoreProvider>().asEagerSingleton()
        bind<Js5MasterIndex>().toProvider<Js5MasterIndexProvider>().asEagerSingleton()
    }
}
