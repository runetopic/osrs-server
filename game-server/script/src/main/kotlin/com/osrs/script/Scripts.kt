package com.osrs.script

import com.google.inject.Injector
import com.osrs.script.content.ContentScript
import com.osrs.script.content.ContentScriptConfiguration
import io.github.classgraph.ClassGraph

object Scripts {

    private const val ACCEPTED_CONTENT_PACKAGE = "com.osrs.content"

    private val contentScripts = mutableListOf<ContentScript>()

    fun loadContentScripts(injector: Injector): List<ContentScript> {
        ClassGraph()
            .acceptPackages(ACCEPTED_CONTENT_PACKAGE)
            .enableAllInfo()
            .scan()
            .use { scan -> scan.getSubclasses(ContentScriptConfiguration::class.java).directOnly().forEach { info ->
                try {
                    contentScripts += info
                        .loadClass(ContentScriptConfiguration::class.java)
                        .getConstructor(Injector::class.java)
                        .newInstance(injector)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
        return contentScripts
    }
}
