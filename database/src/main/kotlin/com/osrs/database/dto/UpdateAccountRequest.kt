package com.osrs.database.dto

import com.osrs.common.item.FloorItem
import com.osrs.common.map.location.Location
import com.osrs.common.skill.Skills

data class UpdateAccountRequest(
    val username: String,
    val skills: Skills,
    val location: Location,
    val objs: List<FloorItem> = emptyList()
)
