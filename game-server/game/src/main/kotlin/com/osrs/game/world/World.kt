package com.osrs.game.world

import com.osrs.game.actor.PlayerList
import com.osrs.game.actor.player.Player
import com.osrs.game.network.Session
import java.util.concurrent.ConcurrentHashMap

data class World(
    val worldId: Int,
    val players: PlayerList = PlayerList(MAX_PLAYERS)
) {
    private val loginRequests = ConcurrentHashMap<Player, Session>()

    fun requestLogin(session: Session, player: Player) {
        this.loginRequests[player] = session
    }

    fun processLoginRequest() {
        loginRequests.entries.take(150).onEach {
            players.add(it.key)
            it.key.login(this)
        }.also(loginRequests.entries::removeAll)
    }

    companion object {
        const val MAX_PLAYERS = 2048
    }
}
