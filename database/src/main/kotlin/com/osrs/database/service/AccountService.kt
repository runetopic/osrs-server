package com.osrs.database.service

import com.google.inject.Inject
import com.google.inject.Singleton
import com.osrs.common.map.location.Location
import com.osrs.database.dto.CreateAccountRequest
import com.osrs.database.entity.Account
import com.osrs.database.repository.AccountRepository
import org.mindrot.jbcrypt.BCrypt

@Singleton
class AccountService @Inject constructor(
    private val accountRepository: AccountRepository
) {
    fun createAccount(request: CreateAccountRequest): Account? {
        val (username, email, password) = request

        if (accountRepository.findAccountByUsername(username) != null) return null

        return accountRepository.createAccount(
            Account(
                username = username,
                rights = 0,
                email = email,
                password = BCrypt.hashpw(password, BCrypt.gensalt(12)),
                location = Location.Default
            )
        )
    }

    fun findAccountByUsername(username: String): Account? = accountRepository.findAccountByUsername(username)

    fun validateAccount(account: Account, password: String): Boolean {
        // TODO: Can add a validation response, depending on if the account has been banned etc.
        return BCrypt.checkpw(password, account.password)
    }
}
