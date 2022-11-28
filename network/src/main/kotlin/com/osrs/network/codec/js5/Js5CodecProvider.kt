package com.osrs.network.codec.js5

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import com.osrs.cache.Cache

@Singleton
class Js5CodecProvider @Inject constructor(
    private val cache: Cache
) : Provider<Js5Codec> {

    override fun get(): Js5Codec = Js5Codec(
        cache
    )
}
