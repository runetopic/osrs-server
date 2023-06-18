package com.osrs.repository.account

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.api.map.location.Location
import com.osrs.database.dto.UpdateAccountRequest
import com.osrs.database.entity.Account
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.mindrot.jbcrypt.BCrypt

@Singleton
class AccountMongoRepository @Inject constructor(
    mongoClient: MongoClient
) : AccountRepository {

    private val collection = mongoClient.getDatabase("api").getCollection<Account>("account")

    init {
        with(collection) {
            ensureUniqueIndex(Account::email, Account::displayName)
            val adminAccount = find(Account::displayName eq "admin")

            if (adminAccount.none()) {
                insertOne(
                    Account(
                        userName = "admin",
                        displayName = "Admin account",
                        rights = 2,
                        email = "admin@xlitersps.com",
                        password = BCrypt.hashpw("password", BCrypt.gensalt(12)),
                        location = Location.Default
                    )
                )
            }
        }
    }

    override fun findAccountByUsername(username: String): Account? = collection.findOne(Account::displayName eq username)

    override fun createAccount(account: Account): Account {
        val created = collection.insertOne(account)
        if (created.insertedId == null) throw InternalError("Failed to create the user in the database. Username: ${account.displayName}")
        return account
    }

    override fun saveAccount(updateAccountRequest: UpdateAccountRequest): Boolean {
        TODO("Not yet implemented")
    }
}
