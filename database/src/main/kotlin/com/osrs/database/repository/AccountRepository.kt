package com.osrs.database.repository

import com.osrs.database.entity.Account

interface AccountRepository {
    fun findAccountByUsername(username: String): Account?
    fun createAccount(account: Account): Account
}
