package com.osrs.cache.entry.config.varbit

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.CONFIG_INDEX
import com.osrs.cache.CacheModule.VARBIT_CONFIG
import com.osrs.cache.entry.EntryTypeProvider
import com.osrs.common.buffer.readUByte
import com.osrs.common.buffer.readUShort
import java.nio.ByteBuffer

@Singleton
class VarBitEntryProvider @Inject constructor(
    val cache: Cache
) : EntryTypeProvider<VarBitEntry>() {
    val mersennePrime: IntArray = generateMersennePrimeNumbers()

    override fun loadTypeMap(): Map<Int, VarBitEntry> = cache
        .index(CONFIG_INDEX)
        .group(VARBIT_CONFIG)
        ?.files()
        ?.map { ByteBuffer.wrap(it.data).loadEntryType(VarBitEntry(it.id)) }
        ?.associateBy(VarBitEntry::id) ?: emptyMap()

    private tailrec fun ByteBuffer.loadEntryType(type: VarBitEntry): VarBitEntry {
        when (val opcode = readUByte()) {
            0 -> { return type }
            1 -> {
                type.index = readUShort()
                type.leastSignificantBit = readUByte()
                type.mostSignificantBit = readUByte()
            }
            else -> throw IllegalArgumentException("Missing opcode $opcode.")
        }
        return loadEntryType(type)
    }

    private companion object {
        fun generateMersennePrimeNumbers(): IntArray {
            val mersennePrime = IntArray(32)
            var i = 2
            (mersennePrime.indices).forEach {
                mersennePrime[it] = i - 1
                i += i
            }
            return mersennePrime
        }
    }
}
