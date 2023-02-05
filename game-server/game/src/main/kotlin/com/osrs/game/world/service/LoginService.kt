package com.osrs.game.world.service

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.entry.config.enum.EnumTypeProvider
import com.osrs.common.ui.InterfaceInfoMap
import com.osrs.game.actor.player.Player
import com.osrs.game.ui.Interfaces

@Singleton
class LoginService @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
    private val enums: EnumTypeProvider
) {
    fun login(player: Player) {
        player.initialize(
            interfaces = Interfaces(player, interfaceInfoMap, enums)
        )
        player.login()
    }
}
