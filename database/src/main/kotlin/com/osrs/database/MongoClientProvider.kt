package com.osrs.database

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import io.ktor.server.application.ApplicationEnvironment
import org.litote.kmongo.KMongo

@Singleton
class MongoClientProvider @Inject constructor(
    environment: ApplicationEnvironment
) : Provider<MongoClient?> {
    private val connectionString = environment.config.propertyOrNull("mongo.connection")?.getString() ?: ""
    private val isLocal = environment.config.property("game.local").getString().toBoolean()

    override fun get(): MongoClient? {
        if (isLocal || connectionString.isEmpty()) return null
        return KMongo.createClient(connectionString)
    }
}
