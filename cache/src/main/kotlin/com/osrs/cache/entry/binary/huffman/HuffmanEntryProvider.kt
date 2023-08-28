package com.osrs.cache.entry.binary.huffman

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.entry.EntryTypeProvider
import com.runetopic.cryptography.huffman.Huffman
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class HuffmanEntryProvider @Inject constructor(
    private val cache: Cache
) : EntryTypeProvider<HuffmanEntry>() {
    override fun loadTypeMap(): Map<Int, HuffmanEntry> =
        emptyMap()

    private fun ByteBuffer.loadEntryType(type: HuffmanEntry): HuffmanEntry = type.apply {
        huffman = Huffman(
            sizes = array(),
            limit = 75
        )
    }
}
