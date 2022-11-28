package com.osrs.network.codec

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.network.codec.game.GameCodec
import com.osrs.network.codec.handshake.HandshakeCodec
import com.osrs.network.codec.js5.Js5Codec
import com.osrs.network.codec.login.LoginCodec

@Singleton
class CodecProvider @Inject constructor(
    private val handshakeCodec: HandshakeCodec,
    private val js5Codec: Js5Codec,
    private val loginCodec: LoginCodec,
    private val gameCodec: GameCodec
) : Provider<Codec> {

    override fun get(): Codec = Codec(
        handshakeCodec,
        js5Codec,
        loginCodec,
        gameCodec
    )
}
