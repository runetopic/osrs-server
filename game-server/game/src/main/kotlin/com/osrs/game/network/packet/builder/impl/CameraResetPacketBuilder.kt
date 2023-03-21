package com.osrs.game.network.packet.builder.impl

import com.google.inject.Singleton
import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.network.packet.type.server.CameraReset

@Singleton
class CameraResetPacketBuilder : PacketBuilder<CameraReset>(
    opcode = 11,
    size = 0
) {
    override fun build(packet: CameraReset, buffer: RSByteBuffer) { }
}
