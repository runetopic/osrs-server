package com.osrs.database.repository

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.map.location.Location
import com.osrs.database.entity.Account
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.mindrot.jbcrypt.BCrypt
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class AccountDiskRepository @Inject constructor(
    environment: ApplicationEnvironment,
    private val json: Json,
): AccountRepository {
    private val playerSaveDirectory = environment.config.propertyOrNull("game.configuration.players")?.getString() ?: "./players/"

    private val logger = InlineLogger()

    init {
        if (findAccountByUsername("admin") == null) {
            createAccount(
                Account(
                    username = "admin",
                    rights = 2,
                    email = "admin@xlitersps.com",
                    password = BCrypt.hashpw("password", BCrypt.gensalt(12)),
                    location = Location.Default
                )
            )
        }
    }

    override fun findAccountByUsername(username: String): Account? {
        val path = Path.of("${playerSaveDirectory}/$username.json")

        if (!path.exists()) return null

        return try {
            json.decodeFromStream(path.inputStream())
        } catch(exception: Exception) {
            logger.error(exception) { "There was a problem loading the account by the username." }
            return null
        }
    }

    override fun createAccount(account: Account): Account {
        val path = Path.of("${playerSaveDirectory}/${account.username}.json")
        json.encodeToStream(account, path.outputStream())
        return account
    }
}
