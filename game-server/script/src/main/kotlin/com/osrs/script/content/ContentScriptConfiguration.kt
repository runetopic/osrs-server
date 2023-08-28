package com.osrs.script.content

import com.google.inject.Injector
import com.osrs.game.clock.GameClock
import com.osrs.game.ui.InterfaceListener
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Kotlin Content Scripts",
    fileExtension = "script.kts"
)
abstract class ContentScriptConfiguration(
    interfaceListener: InterfaceListener,
    gameClock: GameClock
) : ContentScript(
    interfaceListener,
    gameClock
)
