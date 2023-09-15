package com.osrs.cache.server

import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Store

object ServerCacheModule : KotlinModule() {
    override fun configure() {
        bind<Store>().annotatedWith<ServerCache>().toProvider<ServerStoreProvider>()
    }
}
