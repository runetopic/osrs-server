package com.osrs.database

import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.database.account.AccountService
import dev.misfitlabs.kotlinguice4.KotlinModule

@Singleton
object DatabaseModule : KotlinModule() {

    override fun configure() {
        bind<MongoClient>().toProvider<MongoClientProvider>().asEagerSingleton()
        bind<AccountService>().asEagerSingleton()
    }
}
