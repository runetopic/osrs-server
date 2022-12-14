package com.osrs.game.network.packet.builder.impl.sync.block.player

import com.osrs.game.actor.player.Player
import com.osrs.game.actor.render.RenderBlock
import com.osrs.game.network.buffer.writeByteSubtract
import com.osrs.game.network.buffer.writeReversed
import com.osrs.game.network.buffer.writeStringCp1252NullTerminated
import com.osrs.game.network.packet.builder.impl.sync.block.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.BodyPartColor
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.PlayerIdentityKit
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeShort

class PlayerAppearanceBlock : RenderBlockBuilder<Player>(0, 0x4) {

    override fun build(actor: Player): ByteReadPacket = buildPacket {
        val render = actor.appearance

        val data = buildPacket {
            writeByte(render.gender.mask.toByte())
            writeByte(render.skullIcon.toByte())
            writeByte(render.headIcon.toByte())
            if (render.transform != -1) writeTransmog(render) else writeIdentityKit(render)
            writeColors(this, render.bodyPartColors.entries)
            writeAnimations(render)
            writeStringCp1252NullTerminated(actor.username)
            writeByte(126)
            writeShort(0)
            writeByte(0) // Hidden
            writeShort(0)
            repeat(3) { writeStringCp1252NullTerminated("") }
            writeByte(0)
        }
        writeByteSubtract(data.remaining.toByte())
        writeReversed(data.readBytes())
    }

    private fun BytePacketBuilder.writeAnimations(render: RenderBlock.Appearance) = if (render.transform == -1) {
        intArrayOf(808, 823, 819, 820, 821, 822, 824).forEach { writeShort(it.toShort()) }
    } else {
        // TODO NPC transmog
    }

    private fun BytePacketBuilder.writeTransmog(render: RenderBlock.Appearance) {
        writeShort(65535.toShort())
        writeShort(render.transform.toShort())
    }

    private fun BytePacketBuilder.writeIdentityKit(render: RenderBlock.Appearance) = PlayerIdentityKit.values()
        .sortedBy { it.info.index }
        .forEach {
            it.info.build(
                this,
                BodyPartCompanion(
                    render.gender,
                    render.bodyParts.getOrDefault(it.bodyPart, 0)
                )
            )
        }

    private fun writeColors(builder: BytePacketBuilder, colours: Set<Map.Entry<BodyPartColor, Int>>) =
        colours.sortedBy { it.key.id }.forEach { builder.writeByte(it.value.toByte()) }
}
