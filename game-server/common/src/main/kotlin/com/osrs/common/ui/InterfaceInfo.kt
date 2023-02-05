package com.osrs.common.ui

import kotlinx.serialization.Serializable

@Serializable
data class InterfaceInfo(
    val id: Int,
    val name: String,
    val resizableChildId: Int
)
