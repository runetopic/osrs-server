package com.osrs.service

import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.service.account.AccountService
import com.osrs.service.mongo.MongoClientProvider
import com.osrs.service.xtea.XteaService
import dev.misfitlabs.kotlinguice4.KotlinModule

@Singleton
object ServiceModule : KotlinModule() {

    override fun configure() {
        bind<MongoClient>().toProvider<MongoClientProvider>().asEagerSingleton()
        bind<AccountService>().asEagerSingleton()
        bind<XteaService>().asEagerSingleton()
    }
}
