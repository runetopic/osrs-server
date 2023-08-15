package com.osrs.script.content

import com.google.inject.Injector
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "Kotlin Content Scripts",
    fileExtension = "script.kts"
)
abstract class ContentScriptConfiguration(injector: Injector) : ContentScript(injector)
