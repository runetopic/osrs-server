package com.osrs.common.ui

import kotlinx.serialization.Serializable

const val MODAL_CHILD_ID = 16
const val MODAL_CHILD_ID_EXTENDED = 17
const val INVENTORY_CHILD_ID = 73

@Serializable
data class InterfaceInfo(
    val id: Int,
    val name: String,
    val resizableChildId: Int
)

fun InterfaceInfo.isModal() = resizableChildId == MODAL_CHILD_ID || resizableChildId == MODAL_CHILD_ID_EXTENDED
