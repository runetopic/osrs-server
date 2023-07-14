package com.osrs.game.ui

import com.osrs.game.ui.listener.InterfaceListener
import com.osrs.game.ui.listener.impl.InventoryInterfaceListener
import com.osrs.game.ui.listener.impl.SetDisplayNameListener
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMapBinder
import kotlin.reflect.KClass

object UserInterfaceModule : KotlinModule() {
    override fun configure() {
        val interfaceListeners = KotlinMapBinder.newMapBinder<KClass<*>, InterfaceListener<UserInterface>>(kotlinBinder)

        interfaceListeners.addBinding(UserInterface.Inventory::class).to<InventoryInterfaceListener>().asEagerSingleton()
        interfaceListeners.addBinding(UserInterface.SetDisplayName::class).to<SetDisplayNameListener>().asEagerSingleton()
    }
}
