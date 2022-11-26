package com.osrs.network.codec

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

interface ByteChannelCodec {
    suspend fun handle(readChannel: ByteReadChannel, writeChannel: ByteWriteChannel)
}
