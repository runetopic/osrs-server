package com.osrs.service.account

import com.osrs.game.actor.skills.Skills
import com.osrs.game.world.map.Location
import com.osrs.service.account.serializer.LocationSerializer
import com.osrs.service.account.serializer.SkillsSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Account(
    val username: String,
    val email: String,
    val rights: Int = 0,
    @Contextual
    val createDate: Instant = Instant.now(),
    @Serializable(with = LocationSerializer::class)
    val location: Location,
    @Serializable(with = SkillsSerializer::class)
    val skills: Skills = Skills()
)
