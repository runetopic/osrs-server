package com.osrs.repository.account

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.config.ServerConfig
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import java.util.*

@Singleton
class AccountRepositoryProvider @Inject constructor(
    private val serverConfig: ServerConfig,
    private val environment: ApplicationEnvironment,
    private val mongoClient: Optional<MongoClient>
) : Provider<AccountRepository> {

    override fun get(): AccountRepository {
        if (serverConfig.local || !mongoClient.isPresent) {
            return AccountDiskRepository(
                serverConfig,
                Json {
                    serializersModule = IdKotlinXSerializationModule
                    prettyPrint = true
                }
            )
        }

        return AccountMongoRepository(mongoClient.get())
    }
}
