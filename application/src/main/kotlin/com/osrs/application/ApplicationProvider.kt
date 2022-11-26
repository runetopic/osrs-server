package com.osrs.application

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.name.Named
import io.ktor.server.application.Application
import io.ktor.server.engine.commandLineEnvironment

class ApplicationProvider @Inject constructor(
    @Named("args") val args: Array<String>
) : Provider<Application> {
    override fun get(): Application = commandLineEnvironment(args).application
}
