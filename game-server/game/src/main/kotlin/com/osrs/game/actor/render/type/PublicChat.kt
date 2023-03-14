package com.osrs.game.actor.render.type

import com.osrs.game.actor.render.RenderType

/**
 * @author Jordan Abraham
 */
data class PublicChat(
    val rights: Int,
    val color: Int,
    val effect: Int,
    val decompressedSize: Int,
    val compressedBytes: ByteArray,
) : RenderType
