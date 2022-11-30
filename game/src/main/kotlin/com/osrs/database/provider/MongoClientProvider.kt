package com.osrs.database.provider

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import io.ktor.server.application.ApplicationEnvironment
import org.litote.kmongo.KMongo

@Singleton
class MongoClientProvider @Inject constructor(
    private val environment: ApplicationEnvironment
) : Provider<MongoClient> {
    override fun get(): MongoClient = KMongo.createClient(environment.config.property("mongo.connection").getString())
}
