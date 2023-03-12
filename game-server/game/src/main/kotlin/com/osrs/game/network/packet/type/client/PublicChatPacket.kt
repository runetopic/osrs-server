package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

/**
 * @author Jordan Abraham
 */
data class PublicChatPacket(
    val idk: Int,
    val color: Int,
    val effect: Int,
    val length: Int,
    val bytes: ByteArray
) : Packet
