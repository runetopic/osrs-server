package com.osrs.network.codec

import com.osrs.network.Session
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

interface CodecChannelHandler {
    suspend fun handle(session: Session, readChannel: ByteReadChannel, writeChannel: ByteWriteChannel)
}
