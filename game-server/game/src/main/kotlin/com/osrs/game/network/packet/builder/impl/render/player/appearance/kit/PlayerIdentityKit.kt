package com.osrs.game.network.packet.builder.impl.render.player.appearance.kit

import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.ArmInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.CapeInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.FootInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HairInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HandInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.HeadInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.JawInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.LegInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.NeckInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.ShieldInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.TorsoInfo
import com.osrs.game.network.packet.builder.impl.render.player.appearance.kit.info.WeaponInfo

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
