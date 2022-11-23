package com.osrs.cache

import com.google.inject.Scopes
import com.google.inject.Singleton
import dev.misfitlabs.kotlinguice4.KotlinModule

@Singleton
object CacheModule : KotlinModule() {
    override fun configure() {
        bind<Cache>().toProvider<CacheProvider>().`in`(Scopes.SINGLETON)
    }
}
