package com.osrs.game.network.codec

import com.osrs.game.network.Session
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

interface CodecChannelHandler {
    suspend fun handle(session: Session, readChannel: ByteReadChannel, writeChannel: ByteWriteChannel)
}
