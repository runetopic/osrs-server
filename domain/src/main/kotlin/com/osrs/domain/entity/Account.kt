package com.osrs.domain.entity

import com.osrs.api.item.FloorItem
import com.osrs.api.map.location.Location
import com.osrs.api.serializer.InstantSerializer
import com.osrs.api.serializer.LocationSerializer
import com.osrs.api.serializer.SkillsSerializer
import com.osrs.api.skill.Skills
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.Instant

@Serializable
data class Account(
    @Contextual val _id: Id<Account> = newId(),
    val userName: String,
    var displayName: String = "",
    val rights: Int = 0,
    val email: String,
    val password: String,
    @Serializable(with = InstantSerializer::class)
    val createDate: Instant = Instant.now(),
    @Serializable(with = LocationSerializer::class)
    var location: Location,
    @Serializable(with = SkillsSerializer::class)
    var skills: Skills = Skills(),
    var objs: List<FloorItem> = emptyList()
)
