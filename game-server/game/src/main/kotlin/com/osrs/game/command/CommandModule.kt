package com.osrs.game.command

import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import io.github.classgraph.ClassGraph

object CommandModule : KotlinModule() {
    override fun configure() {
        val commands = loadCommands()
        val commandListeners = KotlinMultibinder.newSetBinder<CommandListener>(kotlinBinder)

        commands.forEach {
            commandListeners.addBinding().to(it)
        }
    }

    private fun loadCommands(): List<Class<CommandListener>> {
        return ClassGraph().enableClassInfo().scan().use { result ->
            result.allClasses.filter { it.extendsSuperclass(CommandListener::class.java) }
                .map { it.loadClass() as Class<CommandListener> }
        }
    }
}
