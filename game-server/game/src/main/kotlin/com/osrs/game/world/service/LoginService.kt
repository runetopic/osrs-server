package com.osrs.game.world.service

import com.google.inject.Inject
import com.google.inject.Singleton
//import com.osrs.cache.entry.config.enum.EnumEntryProvider
//import com.osrs.cache.entry.config.obj.ObjEntryProvider
import com.osrs.api.ui.InterfaceInfoMap
import com.osrs.game.actor.player.Player
//import com.osrs.game.container.Inventory
import com.osrs.game.ui.InterfaceListener
import com.osrs.game.ui.Interfaces

@Singleton
class LoginService @Inject constructor(
    private val interfaceInfoMap: InterfaceInfoMap,
//    private val enumEntryProvider: EnumEntryProvider,
//    private val objEntryProvider: ObjEntryProvider,
    private val interfaceListener: InterfaceListener
) {
    fun login(player: Player) {
        player.initialize(
            interfaces = Interfaces(
                player,
                interfaceInfoMap,
//                enumEntryProvider,
                interfaceListener
            ),
//            inventory = Inventory(player, objEntryProvider)
        )
        player.login()
    }
}
