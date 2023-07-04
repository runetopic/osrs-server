package com.osrs.cache.entry.map

import com.osrs.cache.entry.EntryType

data class MapSquareEntry(
    override val id: Int,
    val regionX: Int = id shr 8,
    val regionZ: Int = id and 0xFF,
    val terrain: LongArray = LongArray(4 * AREA), // Lands
    val locations: MutableList<Long> = ArrayList()
) : EntryType(id) {
    fun pack(level: Int, x: Int, z: Int): Int = (x and 0x3F shl 6) or (z and 0x3F) or (level shl 12)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapSquareEntry

        if (id != other.id) return false
        if (regionX != other.regionX) return false
        if (regionZ != other.regionZ) return false
        if (!terrain.contentEquals(other.terrain)) return false
        if (locations != other.locations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + regionX
        result = 31 * result + regionZ
        result = 31 * result + terrain.contentHashCode()
        result = 31 * result + locations.hashCode()
        return result
    }

    companion object {
        const val AREA = 64 * 64
    }
}
