package com.osrs.cache.entry.binary.huffman

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.BINARY_INDEX
import com.osrs.cache.entry.EntryTypeProvider
import com.runetopic.cache.extension.decompress
import com.runetopic.cryptography.huffman.Huffman
import java.nio.ByteBuffer

/**
 * @author Jordan Abraham
 */
@Singleton
class HuffmanEntryProvider @Inject constructor(
    private val cache: Cache
) : EntryTypeProvider<HuffmanEntry>() {
    override fun loadTypeMap(): Map<Int, HuffmanEntry> = cache
        .index(BINARY_INDEX)
        .group(groupName = "huffman")
        ?.file(fileId = 0)
        ?.let { mapOf(it.id to it.data.decompress().buffer.loadEntryType(HuffmanEntry(it.id))) }
        ?: emptyMap()

    private fun ByteBuffer.loadEntryType(type: HuffmanEntry): HuffmanEntry = type.apply {
        huffman = Huffman(
            sizes = array(),
            limit = 75
        )
    }
}
