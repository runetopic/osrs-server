package com.osrs.api.map

import kotlinx.serialization.Serializable

@Serializable
data class NPCSpawnsResource(
    val name: String? = null,
    val id: Int,
    val level: Int,
    val x: Int,
    val z: Int
)
