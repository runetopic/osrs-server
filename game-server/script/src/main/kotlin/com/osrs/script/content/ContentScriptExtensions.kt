package com.osrs.script.content

import com.osrs.game.actor.player.Player
import com.osrs.game.ui.UserInterface
import com.osrs.game.ui.UserInterfaceListener
import kotlin.properties.ObservableProperty

class InjectedProperty<T>(value: T) : ObservableProperty<T>(value)

inline fun <reified T> ContentScript.inject(): InjectedProperty<T> = InjectedProperty(injector.getInstance(T::class.java))

inline fun <reified T : UserInterface> ContentScript.userInterface(noinline listener: (UserInterfaceListener).(Player) -> Unit) = interfaceListener.userInterface<T>(listener)
