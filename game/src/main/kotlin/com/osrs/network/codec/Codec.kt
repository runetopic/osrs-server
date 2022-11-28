package com.osrs.network.codec

import com.google.inject.Inject
import com.osrs.network.codec.impl.GameCodec
import com.osrs.network.codec.impl.HandshakeCodec
import com.osrs.network.codec.impl.Js5Codec
import com.osrs.network.codec.impl.LoginCodec

data class Codec @Inject constructor(
    val handshakeCodec: HandshakeCodec,
    val js5Codec: Js5Codec,
    val loginCodec: LoginCodec,
    val gameCodec: GameCodec
)
