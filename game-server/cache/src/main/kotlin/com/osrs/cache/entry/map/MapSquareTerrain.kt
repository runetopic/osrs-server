package com.osrs.cache.entry.map

@JvmInline
value class MapSquareTerrain(
    val packed: Long
) {
    constructor(
        height: Int,
        overlayId: Int,
        overlayPath: Int,
        overlayRotation: Int,
        collision: Int,
        underlayId: Int
    ) : this (
        (height.toLong() and 0xff)
            or (overlayId.toLong() and 0xffff shl 8)
            or (overlayPath.toLong() and 0xffff shl 24)
            or (overlayRotation.toLong() and 0x3 shl 40)
            or (collision.toLong() and 0x1f shl 42)
            or (underlayId.toLong() and 0xffff shl 47)
    ) {
        require(this.height == height)
        require(this.overlayId == overlayId)
        require(this.overlayPath == overlayPath)
        require(this.overlayRotation == overlayRotation)
        require(this.collision == collision)
        require(this.underlayId == underlayId)
    }

    inline val height: Int
        get() = (packed and 0xff).toInt()

    inline val overlayId: Int
        get() = (packed shr 8 and 0xffff).toInt()

    inline val overlayPath: Int
        get() = (packed shr 24 and 0xffff).toInt()

    inline val overlayRotation: Int
        get() = (packed shr 40 and 0x3).toInt()

    inline val collision: Int
        get() = (packed shr 42 and 0x1f).toInt()

    inline val underlayId: Int
        get() = (packed shr 47 and 0xffff).toInt()
}
