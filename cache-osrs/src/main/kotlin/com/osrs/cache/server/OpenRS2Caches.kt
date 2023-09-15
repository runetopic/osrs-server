package com.osrs.cache.server

import kotlinx.serialization.Serializable

@Serializable
data class OpenRS2Caches(
    val id: Int,
    val game: String,
    val builds: List<OpenRS2Build>
)
