package com.osrs.database.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    val username: String,
    val email: String,
    val password: String,
)
