package com.osrs.service

import com.osrs.service.account.AccountService
import dev.misfitlabs.kotlinguice4.KotlinModule

object ServiceModule : KotlinModule() {

    override fun configure() {
        bind<AccountService>().asEagerSingleton()
    }
}
