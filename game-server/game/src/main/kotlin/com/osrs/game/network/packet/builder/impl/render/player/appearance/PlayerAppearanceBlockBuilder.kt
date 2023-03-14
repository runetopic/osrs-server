package com.osrs.game.network.packet.builder.impl.render.player.appearance

import com.osrs.common.buffer.writeByte
import com.osrs.common.buffer.writeByteAdd
import com.osrs.common.buffer.writeReversedAdd
import com.osrs.common.buffer.writeShort
import com.osrs.common.buffer.writeStringCp1252NullTerminated
import com.osrs.game.actor.render.type.Appearance
import com.osrs.game.network.packet.builder.impl.render.RenderBlockBuilder
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartColor
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.BodyPartCompanion
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.PlayerIdentityKit
import java.nio.ByteBuffer

class PlayerAppearanceBlockBuilder : RenderBlockBuilder<Appearance>(
    index = 1,
    mask = 0x4
) {
    override fun build(buffer: ByteBuffer, render: Appearance) {
        val block = ByteBuffer.allocate(size(render) - 1).apply {
            writeByte(render.gender.mask)
            writeByte(render.skullIcon)
            writeByte(render.headIcon)
            if (render.transform != -1) writeTransmog(render) else writeIdentityKit(render)
            writeColors(render.bodyPartColors.entries)
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

    private fun ByteBuffer.writeAnimations(render: Appearance) = if (render.transform != -1) {
        // TODO NPC transmog
    } else {
        intArrayOf(808, 823, 819, 820, 821, 822, 824).forEach { writeShort(it) }
    }

    private fun ByteBuffer.writeTransmog(render: Appearance) {
        writeShort(65535)
        writeShort(render.transform)
    }

    private fun ByteBuffer.writeIdentityKit(render: Appearance) = enumValues<PlayerIdentityKit>()
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

    private fun ByteBuffer.writeColors(colours: Set<Map.Entry<BodyPartColor, Int>>) =
        colours.sortedBy { it.key.id }.forEach { writeByte(it.value) }

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
            val jaw = if (render.gender == Appearance.Gender.MALE) 2 else 1
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
