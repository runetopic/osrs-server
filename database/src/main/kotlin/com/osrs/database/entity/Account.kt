package com.osrs.database.entity

import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.Location
import com.osrs.common.map.location.LocationSerializer
import com.osrs.common.skill.Skills
import com.osrs.database.serializer.InstantSerializer
import com.osrs.database.serializer.SkillsSerializer
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
    var objs: List<FloorItem> = emptyList(),
)
