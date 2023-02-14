package com.osrs.game.network.packet.reader.impl

import com.google.inject.Singleton

@Singleton
class IfButton1PacketReader : IfButtonPacketReader(
    opcode = 62,
    size = 8,
    index = 1
)

@Singleton
class IfButton2PacketReader : IfButtonPacketReader(
    opcode = 55,
    size = 8,
    index = 2
)

@Singleton
class IfButton3PacketReader : IfButtonPacketReader(
    opcode = 18,
    size = 8,
    index = 3
)

@Singleton
class IfButton4PacketReader : IfButtonPacketReader(
    opcode = 22,
    size = 8,
    index = 4
)

@Singleton
class IfButton5PacketReader : IfButtonPacketReader(
    opcode = 85,
    size = 8,
    index = 5
)

@Singleton
class IfButton6PacketReader : IfButtonPacketReader(
    opcode = 75,
    size = 8,
    index = 6
)


@Singleton
class IfButton7PacketReader : IfButtonPacketReader(
    opcode = 78,
    size = 8,
    index = 7
)

@Singleton
class IfButton8PacketReader : IfButtonPacketReader(
    opcode = 57,
    size = 8,
    index = 8
)

@Singleton
class IfButton9PacketReader : IfButtonPacketReader(
    opcode = 102,
    size = 8,
    index = 9
)

@Singleton
class IfButton10PacketReader : IfButtonPacketReader(
    opcode = 25,
    size = 8,
    index = 9
)
