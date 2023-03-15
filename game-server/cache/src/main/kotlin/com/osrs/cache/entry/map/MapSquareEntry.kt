package com.osrs.cache.entry.map

import com.osrs.cache.entry.EntryType

data class MapSquareEntry(
    override val id: Int,
    val regionX: Int = id shr 8,
    val regionZ: Int = id and 0xFF,
    val terrain: Array<MapSquareTerrain?> = arrayOfNulls(Short.MAX_VALUE.toInt()),
    val locations: Array<Array<MapSquareLocation?>?> = arrayOfNulls(Short.MAX_VALUE.toInt())
) : EntryType(id) {
    fun pack(level: Int, x: Int, z: Int): Int = (x and 0x3F shl 6) or (z and 0x3F) or (level shl 12)
}
