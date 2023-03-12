package com.osrs.game.network.packet.type.client

/**
 * @author Jordan Abraham
 */
data class PublicChatPacket(
    val idk: Int,
    val color: Int,
    val effect: Int,
    val length: Int,
    val bytes: ByteArray
)
