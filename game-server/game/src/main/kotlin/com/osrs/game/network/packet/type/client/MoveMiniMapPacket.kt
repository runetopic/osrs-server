package com.osrs.game.network.packet.type.client

import com.osrs.game.network.packet.Packet

data class MoveMiniMapPacket(
    val destinationX: Int,
    val destinationZ: Int,
    val movementType: Int,
    val mouseClickedX: Int,
    val mouseClickedZ: Int,
    val cameraAngleZ: Int,
    val value1: Int,
    val value2: Int,
    val value3: Int,
    val value4: Int,
    val currentX: Int,
    val currentZ: Int,
    val value5: Int,
) : Packet
