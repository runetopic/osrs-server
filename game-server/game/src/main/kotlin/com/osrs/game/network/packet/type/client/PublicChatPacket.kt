package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

/**
 * @author Jordan Abraham
 */
data class PublicChatPacket(
    val unknown1: Int,
    val color: Int,
    val effect: Int,
    val compressedSize: Int,
    val compressedBytes: ByteArray,
    val unknown2: Int
) : Packet
