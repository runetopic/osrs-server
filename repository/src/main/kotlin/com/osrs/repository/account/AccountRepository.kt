package com.osrs.repository.account

import com.osrs.domain.dto.UpdateAccountRequest
import com.osrs.domain.entity.Account

interface AccountRepository {
    fun findAccountByUsername(username: String): Account?
    fun createAccount(account: Account): Account
    fun saveAccount(updateAccountRequest: UpdateAccountRequest): Boolean
}
