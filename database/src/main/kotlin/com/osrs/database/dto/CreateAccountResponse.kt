package com.osrs.database.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountResponse(
    val message: String,
)
