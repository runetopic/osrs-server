package com.osrs.cache.vanilla

import dev.misfitlabs.kotlinguice4.KotlinModule
import org.openrs2.cache.Store

object VanillaCacheModule : KotlinModule() {
    override fun configure() {
        bind<Store>().annotatedWith<VanillaCache>().toProvider<VanillaStoreProvider>()
    }
}
