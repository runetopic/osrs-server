package com.osrs.database.repository

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.util.*

@Singleton
class AccountRepositoryProvider @Inject constructor(
    private val environment: ApplicationEnvironment,
    private val mongoClient: Optional<MongoClient>
) : Provider<AccountRepository> {
    private val isLocal = environment.config.property("game.local").getString().toBoolean()

    override fun get(): AccountRepository {
        if (isLocal || !mongoClient.isPresent) {
            return AccountDiskRepository(environment, Json {
                serializersModule = IdKotlinXSerializationModule
                prettyPrint = true
            })
        }

        return AccountMongoRepository(mongoClient.get())
    }
}

