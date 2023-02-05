package com.osrs.common.util

fun Int.packInterface(childId: Int = 0) = this shl 16 or childId
