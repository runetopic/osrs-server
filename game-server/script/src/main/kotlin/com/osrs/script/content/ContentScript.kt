package com.osrs.script.content

import com.google.inject.Injector
import com.osrs.game.ui.InterfaceListener

open class ContentScript(val injector: Injector) {
    val interfaceListener: InterfaceListener by inject()
}
