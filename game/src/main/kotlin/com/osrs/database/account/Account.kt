package com.osrs.database.account

import com.osrs.database.account.serializer.InstantSerializer
import com.osrs.game.actor.skills.Skills
import com.osrs.game.world.map.Location
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant

@Serializable
data class Account(
    @Contextual val _id: Id<Account> = newId(),
    val username: String,
    val rights: Int = 0,
    val email: String,
    val password: String,
    @Serializable(with = InstantSerializer::class)
    val createDate: Instant = Instant.now(),
    val location: Location,
    val skills: Skills = Skills()
)
