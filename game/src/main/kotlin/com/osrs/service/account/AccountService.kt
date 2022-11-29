package com.osrs.service.account

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import com.osrs.game.world.map.Location
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

@Singleton
class AccountService @Inject constructor(
    mongoClient: MongoClient
) {
    private val database = mongoClient.getDatabase("api")
    private val collection = database.getCollection<Account>("account")

    init {
        with(collection) {
            ensureUniqueIndex(Account::email)
            if (find(Account::username eq "ssh").none()) {
                insertOne(
                    Account(
                        username = "ssh",
                        rights = 2,
                        email = "xlitersps@gmail.com",
                        location = Location(3222, 3222, 0)
                    )
                )
            }
        }
    }
}
