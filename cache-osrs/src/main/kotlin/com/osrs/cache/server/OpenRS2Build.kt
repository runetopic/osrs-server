package com.osrs.cache.server

import kotlinx.serialization.Serializable

@Serializable
data class OpenRS2Build(
    val major: Int?,
    val minor: Int?
)
