package com.osrs.network.codec

import com.google.inject.Singleton
import com.osrs.network.codec.impl.GameCodec
import com.osrs.network.codec.impl.HandshakeCodec
import com.osrs.network.codec.impl.Js5Codec
import com.osrs.network.codec.impl.LoginCodec
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder

@Singleton
object CodecModule : KotlinModule() {
    override fun configure() {
        val codecs = KotlinMultibinder.newSetBinder<CodecChannelHandler>(kotlinBinder)

        codecs.addBinding().to<Js5Codec>().asEagerSingleton()
        codecs.addBinding().to<HandshakeCodec>().asEagerSingleton()
        codecs.addBinding().to<LoginCodec>().asEagerSingleton()
        codecs.addBinding().to<GameCodec>().asEagerSingleton()
    }
}
