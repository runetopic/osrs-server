package com.osrs.cache.vanilla

import kotlinx.serialization.Serializable

@Serializable
data class OpenRS2Build(
    val major: Int?,
    val minor: Int?
)
