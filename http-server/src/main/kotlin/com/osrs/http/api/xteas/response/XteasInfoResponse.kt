package com.osrs.http.api.xteas.response

import com.osrs.database.xtea.Xtea
import kotlinx.serialization.Serializable

@Serializable
data class XteasInfoResponse(
    val build: Int,
    val xteas: List<Xtea>
)
