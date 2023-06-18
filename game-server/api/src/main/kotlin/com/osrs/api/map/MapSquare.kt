package com.osrs.api.map

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapSquare(
    @SerialName("mapsquare")
    val id: Int,
    val archive: Int,
    val group: Int,
    @SerialName("name_hash")
    val nameHash: Int,
    val name: String,
    val key: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapSquare

        if (id != other.id) return false
        if (archive != other.archive) return false
        if (group != other.group) return false
        if (nameHash != other.nameHash) return false
        if (name != other.name) return false
        if (!key.contentEquals(other.key)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + archive
        result = 31 * result + group
        result = 31 * result + nameHash
        result = 31 * result + name.hashCode()
        result = 31 * result + key.contentHashCode()
        return result
    }
}
