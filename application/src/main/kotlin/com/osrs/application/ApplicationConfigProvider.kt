package com.osrs.application

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig

class ApplicationConfigProvider @Inject constructor(
    private val environment: ApplicationEnvironment
): Provider<ApplicationConfig> {
    override fun get(): ApplicationConfig = environment.config
}
