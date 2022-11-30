package com.osrs.http.api.account.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountResponse(
    val message: String,
)
