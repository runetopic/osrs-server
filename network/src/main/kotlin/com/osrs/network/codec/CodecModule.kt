package com.osrs.network.codec

import com.osrs.network.codec.game.GameCodec
import com.osrs.network.codec.game.GameCodecProvider
import com.osrs.network.codec.handshake.HandshakeCodec
import com.osrs.network.codec.handshake.HandshakeCodecProvider
import com.osrs.network.codec.js5.Js5Codec
import com.osrs.network.codec.js5.Js5CodecProvider
import com.osrs.network.codec.login.LoginCodec
import com.osrs.network.codec.login.LoginCodecProvider
import dev.misfitlabs.kotlinguice4.KotlinModule

object CodecModule : KotlinModule() {
    override fun configure() {
        bind<HandshakeCodec>().toProvider<HandshakeCodecProvider>().asEagerSingleton()
        bind<Js5Codec>().toProvider<Js5CodecProvider>().asEagerSingleton()
        bind<LoginCodec>().toProvider<LoginCodecProvider>().asEagerSingleton()
        bind<GameCodec>().toProvider<GameCodecProvider>().asEagerSingleton()
        bind<Codec>().toProvider<CodecProvider>().asEagerSingleton()
    }
}
