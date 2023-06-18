package com.osrs.repository

import com.google.inject.Singleton
import com.google.inject.multibindings.OptionalBinder
import com.mongodb.client.MongoClient
import com.osrs.repository.account.AccountRepository
import com.osrs.repository.account.AccountRepositoryProvider
import com.osrs.repository.client.MongoClientProvider
import dev.misfitlabs.kotlinguice4.KotlinModule

@Singleton
object RepositoryModule : KotlinModule() {

    override fun configure() {
        OptionalBinder.newOptionalBinder(kotlinBinder, MongoClient::class.java)
            .setDefault()
            .toProvider(MongoClientProvider::class.java)
            .asEagerSingleton()

        bind<AccountRepository>().toProvider<AccountRepositoryProvider>()
    }
}
