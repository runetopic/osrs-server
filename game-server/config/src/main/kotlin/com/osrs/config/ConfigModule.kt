package com.osrs.config

import dev.misfitlabs.kotlinguice4.KotlinModule

object ConfigModule : KotlinModule() {

    override fun configure() {
        bind<GameConfig>().toProvider<GameConfigProvider>()
    }
}
