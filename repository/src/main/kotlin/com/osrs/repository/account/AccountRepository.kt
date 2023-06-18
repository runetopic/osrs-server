package com.osrs.repository.account

import com.osrs.database.dto.UpdateAccountRequest
import com.osrs.database.entity.Account

interface AccountRepository {
    fun findAccountByUsername(username: String): Account?
    fun createAccount(account: Account): Account
    fun saveAccount(updateAccountRequest: UpdateAccountRequest): Boolean
}
