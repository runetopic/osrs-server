package com.osrs.game.ui

import com.osrs.game.ui.listener.InterfaceListener
import com.osrs.game.ui.listener.impl.InventoryInterfaceListener
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import kotlin.reflect.KClass

object UserInterfaceModule : KotlinModule() {
    override fun configure() {
        val interfaceListeners = KotlinMapBinder.newMapBinder<KClass<*>, InterfaceListener<*>>(kotlinBinder)

        interfaceListeners.addBinding(UserInterface.Inventory::class).to<InventoryInterfaceListener>().asEagerSingleton()
    }
}
