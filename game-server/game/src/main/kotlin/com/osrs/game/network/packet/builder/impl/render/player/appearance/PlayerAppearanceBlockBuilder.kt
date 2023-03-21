package com.osrs.game.network.packet.builder.impl.render.player.appearance

import com.osrs.common.buffer.RSByteBuffer
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.ArmsBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.BackBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.FeetBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HairBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HandsBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HeadBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.JawBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.LegsBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.MainHandBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.NeckBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.OffHandBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.TorsoBuilder
import java.nio.ByteBuffer

class PlayerAppearanceBlockBuilder : RenderBlockBuilder<Appearance>(
    index = 1,
    mask = 0x4
) {
    private val builders = arrayOf(
        HeadBuilder(),
        BackBuilder(),
        NeckBuilder(),
        MainHandBuilder(),
        TorsoBuilder(),
        OffHandBuilder(),
        ArmsBuilder(),
        LegsBuilder(),
        HairBuilder(),
        HandsBuilder(),
        FeetBuilder(),
        JawBuilder()
    )

    override fun build(buffer: RSByteBuffer, render: Appearance) {
        val block = RSByteBuffer(ByteBuffer.allocate(size(render) - 1)).apply {
            writeByte(render.gender.id)
            writeByte(render.skullIcon)
            writeByte(render.headIcon)
            if (render.transform != -1) {
                writeTransmog(render)
            } else {
                writeIdentityKit(render)
            }
            writeColors(render.bodyPartColors)
            writeAnimations(render)
            writeStringCp1252NullTerminated(render.displayName)
            writeByte(126)
            writeShort(0)
            writeByte(0) // Hidden
            writeShort(0)
            repeat(3) { writeStringCp1252NullTerminated("") }
            writeByte(0)
        }
        buffer.writeByteAdd(block.position())
        buffer.writeReversedAdd(block.array())
    }

    private fun RSByteBuffer.writeAnimations(render: Appearance) = if (render.transform != -1) {
        // TODO NPC transmog
    } else {
        render.renderSequences.forEach { writeShort(it) }
    }

    private fun RSByteBuffer.writeTransmog(render: Appearance) {
        writeShort(65535)
        writeShort(render.transform)
    }

    private fun RSByteBuffer.writeIdentityKit(render: Appearance) {
        // If a builder is not of an appearance body part, we send 0 as default
        // but this will be overwritten with their equipped item if applicable.
        builders.forEach { it.build(this, render.gender, render.bodyParts.getOrNull(it.appearanceIndex) ?: 0) }
    }

    private fun RSByteBuffer.writeColors(colors: IntArray) {
        colors.forEach { writeByte(it) }
    }

    override fun size(render: Appearance): Int {
        val gender = 1
        val skull = 1
        val headIcon = 1
        val identity = if (render.transform != -1) {
            4
        } else {
            val arm = 2
            val cape = 1
            val foot = 2
            val hair = 2
            val hand = 2
            val head = 1
            val jaw = if (render.isMale()) 2 else 1
            val leg = 2
            val neck = 1
            val shield = 1
            val torso = 2
            val weapon = 1
            arm + cape + foot + hair + hand + head + jaw + leg + neck + shield + torso + weapon
        }
        val colors = 5
        val animations = if (render.transform != -1) 0 else 14
        val displayName = render.displayName.length + 1
        val combatLevel = 1
        val unknown1 = 2
        val hidden = 1
        val unknown2 = 2
        val strings = 3 * ("".length + 1)
        val unknown3 = 1
        return 1 + gender + skull + headIcon + identity + colors + animations + displayName + combatLevel + unknown1 + hidden + unknown2 + strings + unknown3
    }
}
