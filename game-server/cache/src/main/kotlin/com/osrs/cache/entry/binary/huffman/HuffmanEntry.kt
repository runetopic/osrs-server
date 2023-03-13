package com.osrs.cache.entry.binary.huffman

import com.osrs.cache.entry.EntryType
import com.runetopic.cryptography.huffman.Huffman

/**
 * @author Jordan Abraham
 */
data class HuffmanEntry(
    override val id: Int,
    var huffman: Huffman? = null,
) : EntryType(id)
