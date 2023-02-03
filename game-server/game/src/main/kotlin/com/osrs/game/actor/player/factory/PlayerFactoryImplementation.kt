package com.osrs.game.actor.player.factory

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.cache.Cache
import com.osrs.common.ui.InterfaceInfoMap
import com.osrs.database.entity.Account
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session
import com.osrs.game.ui.Interfaces
import com.osrs.game.world.World

@Singleton
class PlayerFactoryImplementation @Inject constructor(
    private val world: World,
    private val interfaceInfoMap: InterfaceInfoMap,
    private val cache: Cache
) : PlayerFactory {

    override fun buildPlayer(account: Account, session: Session): Player {
        val player = Player(
            location = account.location,
            username = account.username,
            world = world,
            session = session,
        )

        player.interfaces = Interfaces(player, interfaceInfoMap, cache.enums)
        return player
    }
}
