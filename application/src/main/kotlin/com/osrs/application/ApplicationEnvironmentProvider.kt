package com.osrs.application

import com.google.inject.Inject
import com.google.inject.Provider
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment

class ApplicationEnvironmentProvider @Inject constructor(private val application: Application) : Provider<ApplicationEnvironment> {
    override fun get(): ApplicationEnvironment = application.environment
}
