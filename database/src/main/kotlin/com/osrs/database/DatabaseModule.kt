package com.osrs.database

import com.google.inject.Singleton
import com.google.inject.multibindings.OptionalBinder
import com.mongodb.client.MongoClient
import com.osrs.database.repository.AccountRepository
import com.osrs.database.repository.AccountRepositoryProvider
import com.osrs.database.service.AccountService
import dev.misfitlabs.kotlinguice4.KotlinModule

@Singleton
object DatabaseModule : KotlinModule() {

    override fun configure() {
        OptionalBinder.newOptionalBinder(kotlinBinder, MongoClient::class.java)
            .setDefault()
            .toProvider(MongoClientProvider::class.java)
            .asEagerSingleton()

        bind<AccountRepository>().toProvider<AccountRepositoryProvider>()
        bind<AccountService>().asEagerSingleton()
    }
}
