package com.osrs.cache

import com.osrs.cache.js5.Js5CacheModule
import com.osrs.cache.vanilla.VanillaCacheModule
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Cache

object CacheModule : KotlinModule() {

    override fun configure() {
        bind<Cache>().toProvider<CacheProvider>().asEagerSingleton()
        install(VanillaCacheModule)
        install(Js5CacheModule)
    }
}
