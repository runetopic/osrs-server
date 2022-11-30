package com.osrs.database.account

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.game.world.map.Location
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.mindrot.jbcrypt.BCrypt

@Singleton
class AccountService @Inject constructor(
    mongoClient: MongoClient
) {
    private val database = mongoClient.getDatabase("api")
    private val collection = database.getCollection<Account>("account")

    init {
        with(collection) {
            ensureUniqueIndex(Account::email, Account::username)
            val adminAccount = find(Account::username eq "admin")

            if (adminAccount.none()) {
                insertOne(
                    Account(
                        username = "admin",
                        rights = 2,
                        email = "admin@xlitersps.com",
                        password = BCrypt.hashpw("password", BCrypt.gensalt(12)),
                        location = Location(3222, 3222, 0)
                    )
                )
            }
        }
    }

    fun createAccount(username: String, email: String, password: String): Account? {
        if (collection.findOne(Account::username eq username) != null) return null

        val account = Account(
            username = username,
            rights = 0,
            email = email,
            password = BCrypt.hashpw(password, BCrypt.gensalt(12)),
            location = Location.Default
        )

        if (collection.insertOne(account).insertedId == null) throw InternalError("Failed to create account.")

        return account
    }

    fun validateCredentials(username: String, password: String): Boolean {
        val account = collection.findOne(Account::username eq username) ?: return false
        return BCrypt.checkpw(password, account.password)
    }
}
