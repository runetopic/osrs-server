package com.osrs.cache.entry.map

import com.osrs.cache.entry.EntryType

class MapSquareEntry(
    override val id: Int
) : EntryType(id) {
    val regionX get() = id shr 8
    val regionZ get() = id and 0xFF
    val terrain: Array<Array<Array<MapSquareTerrain?>>> = Array(LEVELS) { Array(MAP_SIZE) { arrayOfNulls(MAP_SIZE) } }
    val locations: Array<Array<Array<MutableList<MapSquareLocation>>>> = Array(LEVELS) { Array(MAP_SIZE) { Array(MAP_SIZE) { mutableListOf() } } }

    companion object {
        const val LEVELS = 4
        const val MAP_SIZE = 64

        const val BLOCKED_TILE_BIT = 0x1
        const val BRIDGE_TILE_BIT = 0x2
    }
}
