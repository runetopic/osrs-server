package com.osrs.game.network.packet.builder.impl.sync.block.player.kit

import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.ArmInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.CapeInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.FootInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.HairInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.HandInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.HeadInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.JawInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.LegInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.NeckInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.ShieldInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.TorsoInfo
import com.osrs.game.network.packet.builder.impl.sync.block.player.kit.impl.WeaponInfo

enum class PlayerIdentityKit(
    val info: BodyPartInfo,
    val bodyPart: BodyPart? = null
) {
    HAIR(HairInfo(), BodyPart.HEAD),
    JAW(JawInfo(), BodyPart.JAW),
    TORSO(TorsoInfo(), BodyPart.TORSO),
    ARMS(ArmInfo(), BodyPart.ARMS),
    HANDS(HandInfo(), BodyPart.HANDS),
    LEGS(LegInfo(), BodyPart.LEGS),
    FEET(FootInfo(), BodyPart.FEET),
    SHIELD(ShieldInfo()),
    HEAD(HeadInfo()),
    CAPE(CapeInfo()),
    NECK(NeckInfo()),
    WEAPON(WeaponInfo());
}
