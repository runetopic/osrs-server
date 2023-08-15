package com.osrs.game.ui

import com.osrs.game.actor.player.Player

class InterfaceListener : MutableMap<Class<*>, (UserInterfaceListener).(Player) -> Unit> by mutableMapOf() {

    internal fun createListener(userInterface: UserInterface, player: Player): UserInterfaceListener {
        val listener = UserInterfaceListener(player, userInterface)
        this[userInterface::class.java]?.invoke(listener, player)
        return listener
    }

    inline fun <reified T : UserInterface> userInterface(noinline listener: (UserInterfaceListener).(Player) -> Unit) {
        this[T::class.java] = listener
    }
}

