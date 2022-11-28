package com.osrs.network.codec

import com.osrs.network.codec.game.GameCodec
import com.osrs.network.codec.handshake.HandshakeCodec
import com.osrs.network.codec.js5.Js5Codec
import com.osrs.network.codec.login.LoginCodec

data class Codec(
    val handshakeCodec: HandshakeCodec,
    val js5Codec: Js5Codec,
    val loginCodec: LoginCodec,
    val gameCodec: GameCodec
)
