package com.osrs.game.network.packet.handler.impl

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.entry.binary.huffman.HuffmanEntryProvider
import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.type.PublicChat
import com.osrs.game.network.packet.handler.PacketHandler
import com.osrs.game.network.packet.type.client.PublicChatPacket
import com.runetopic.cryptography.compressHuffman
import com.runetopic.cryptography.decompressHuffman

/**
 * @author Jordan Abraham
 */
@Singleton
class PublicChatPacketHandler @Inject constructor(
    huffmanEntryProvider: HuffmanEntryProvider
) : PacketHandler<PublicChatPacket>() {
    private val huffman = huffmanEntryProvider[0]?.huffman

    override fun handlePacket(packet: PublicChatPacket, player: Player) {
        if (huffman == null) return
        val formatted = packet.compressedBytes.decompressHuffman(huffman, packet.decompressedSize).decodeToString().trim()
        val compressed = formatted.encodeToByteArray().compressHuffman(huffman)
        check(compressed.contentEquals(packet.compressedBytes))
        player.renderer.publicChat(PublicChat(packet.color, packet.effect, formatted.length, compressed))
    }
}
