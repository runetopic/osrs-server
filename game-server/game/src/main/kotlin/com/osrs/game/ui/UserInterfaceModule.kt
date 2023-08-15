package com.osrs.game.ui

import dev.misfitlabs.kotlinguice4.KotlinModule

object UserInterfaceModule : KotlinModule() {
    override fun configure() {
        bind<InterfaceListener>().toInstance(InterfaceListener())
    }
}
