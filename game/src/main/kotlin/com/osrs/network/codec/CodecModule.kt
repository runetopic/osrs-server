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

        codecs.addBinding().to<Js5Codec>()
        codecs.addBinding().to<HandshakeCodec>()
        codecs.addBinding().to<LoginCodec>()
        codecs.addBinding().to<GameCodec>()
    }
}
