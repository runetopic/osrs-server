package com.osrs.api.util

fun Int.packInterface(childId: Int = 0) = this shl 16 or childId
fun Int.interfaceId() = this shr 16
fun Int.childId() = this and 0xFFFF
