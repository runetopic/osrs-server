package com.osrs.service

import com.osrs.service.xtea.XteaService
import dev.misfitlabs.kotlinguice4.KotlinModule

object ServiceModule : KotlinModule() {

    override fun configure() {
        bind<XteaService>().asEagerSingleton()
    }
}
