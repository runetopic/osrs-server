package com.osrs.script

import com.google.inject.Injector
import com.osrs.game.clock.GameClock
import com.osrs.game.ui.InterfaceListener
import com.osrs.script.content.ContentScript
import com.osrs.script.content.ContentScriptConfiguration
import dev.misfitlabs.kotlinguice4.getInstance
import io.github.classgraph.ClassGraph

object Scripts {

    private const val ACCEPTED_CONTENT_PACKAGE = "com.osrs.content"

    private val contentScripts = mutableListOf<ContentScript>()

    fun loadContentScripts(injector: Injector): List<ContentScript> {
        val interfaceListener = injector.getInstance<InterfaceListener>()
        val gameClock = injector.getInstance<GameClock>()

        val ctorTypes = arrayOf<Class<*>>(
            interfaceListener::class.java,
            gameClock::class.java
        )

        val ctorValues = arrayOf(
            interfaceListener,
            gameClock
        )

        ClassGraph()
            .acceptPackages(ACCEPTED_CONTENT_PACKAGE)
            .enableAllInfo()
            .scan()
            .use { scan -> scan.getSubclasses(ContentScriptConfiguration::class.java).directOnly().forEach { info ->
                try {
                    contentScripts += info
                        .loadClass(ContentScriptConfiguration::class.java)
                        .getConstructor(*ctorTypes)
                        .newInstance(*ctorValues)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
        return contentScripts
    }
}
