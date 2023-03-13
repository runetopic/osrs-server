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
    huffmanEntryProvider: HuffmanEntryProvider,
) : PacketHandler<PublicChatPacket>() {
    private val huffman = huffmanEntryProvider[0]?.huffman

    override fun handlePacket(packet: PublicChatPacket, player: Player) {
        if (huffman == null) return
        // Format incoming public chat message to our standards.
        val formatted = packet.compressedBytes.decompressHuffman(huffman, packet.decompressedSize).decodeToString().trim()
        // Compress the formatted public chat message.
        val compressed = formatted.encodeToByteArray().compressHuffman(huffman)
        player.renderer.update(PublicChat(packet.color, packet.effect, formatted.length, compressed))
    }
}
