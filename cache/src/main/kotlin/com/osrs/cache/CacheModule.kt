package com.osrs.cache

import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule

object CacheModule : KotlinModule() {
    override fun configure() {
        bind<Cache>().toProvider<CacheProvider>().`in`<Singleton>()
    }
}
