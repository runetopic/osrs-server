package com.osrs.game.actor.player.factory

import com.osrs.database.entity.Account
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session

interface PlayerFactory {
    fun buildPlayer(account: Account, session: Session): Player
}
