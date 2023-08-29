package com.osrs.cache.js5

import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Js5MasterIndex
import org.openrs2.cache.Store

object Js5CacheModule : KotlinModule() {

    override fun configure() {
        bind<Store>().toProvider<Js5StoreProvider>().asEagerSingleton()
        bind<Js5MasterIndex>().toProvider<Js5MasterIndexProvider>().asEagerSingleton()
    }
}
