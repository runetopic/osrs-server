package com.osrs.script.content

import com.osrs.game.actor.player.Player
import com.osrs.game.clock.GameClock
import com.osrs.game.ui.InterfaceListener
import com.osrs.game.ui.UserInterface
import com.osrs.game.ui.UserInterfaceListener

open class ContentScript(
    val interfaceListener: InterfaceListener,
    gameClock: GameClock
) {
    val gameClockTick = gameClock.current()

    inline fun <reified T : UserInterface> userInterface(noinline listener: (UserInterfaceListener).(Player) -> Unit) = interfaceListener.userInterface<T>(listener)
}
