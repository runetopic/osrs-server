package com.osrs.database.dto

import com.osrs.api.item.FloorItem
import com.osrs.api.map.location.Location
import com.osrs.api.skill.Skills

data class UpdateAccountRequest(
    val userName: String,
    val displayName: String,
    val skills: Skills,
    val location: Location,
    val objs: List<FloorItem> = emptyList()
)
