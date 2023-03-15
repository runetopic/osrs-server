package com.osrs.cache.entry.map

import com.osrs.cache.entry.EntryType

data class MapSquareEntry(
    override val id: Int,
    val regionX: Int = id shr 8,
    val regionZ: Int = id and 0xFF,
    val terrain: Array<MapSquareTerrain?> = arrayOfNulls(4 * 64 * 64),
    val locations: Array<Array<MapSquareLocation?>?> = arrayOfNulls(4 * 64 * 64)
) : EntryType(id) {
    fun pack(level: Int, x: Int, z: Int): Int = (x and 0x3F shl 6) or (z and 0x3F) or (level shl 12)
}
