package com.osrs.database.repository

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.common.map.location.Location
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
            ensureUniqueIndex(Account::email, Account::username)
            val adminAccount = find(Account::username eq "admin")

            if (adminAccount.none()) {
                insertOne(
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
    }

    override fun findAccountByUsername(username: String): Account? = collection.findOne(Account::username eq username)

    override fun createAccount(account: Account): Account {
        val created = collection.insertOne(account)
        if (created.insertedId == null) throw InternalError("Failed to create the user in the database. Username: ${account.username}")
        return account
    }
}
