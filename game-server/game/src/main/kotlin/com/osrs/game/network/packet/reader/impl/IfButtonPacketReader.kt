package com.osrs.game.network.packet.reader.impl

import com.osrs.api.buffer.readUShort
import com.osrs.game.network.packet.reader.PacketReader
import com.osrs.game.network.packet.type.client.IfButtonPacket
import io.ktor.utils.io.ByteReadChannel

open class IfButtonPacketReader(
    override val opcode: Int,
    override val size: Int,
    val index: Int
) : PacketReader<IfButtonPacket>(opcode, size) {

    override suspend fun read(readChannel: ByteReadChannel, size: Int): IfButtonPacket = IfButtonPacket(
        index = index,
        packedInterface = readChannel.readInt(),
        slotId = readChannel.readUShort(),
        itemId = readChannel.readUShort()
    )
}
