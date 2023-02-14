package com.osrs.common.map.location

@JvmInline
value class LocalLocation(val packedLocation: Int) {
    inline val x get() = packedLocation shr 8
    inline val z get() = packedLocation and 0xff
    inline val offsetX get() = x - ((x shr 3) shl 3)
    inline val offsetZ get() = z - ((z shr 3) shl 3)
    inline val packedOffset get() = (offsetX shl 4) or offsetZ
}
