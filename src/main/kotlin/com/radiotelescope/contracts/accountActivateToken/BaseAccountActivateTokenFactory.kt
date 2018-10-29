package com.radiotelescope.contracts.accountActivateToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Base concrete implementation of the [AccountActivateTokenFactory] interface
 *
 * @param accountActivateTokenRepo the [IAccountActivateTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class BaseAccountActivateTokenFactory (
        private val accountActivateTokenRepo: IAccountActivateTokenRepository,
        private val userRepo: IUserRepository
) : AccountActivateTokenFactory {
    /**
     * Override of the [AccountActivateTokenFactory.activateAccount] method that
     * will return a [ActivateAccount] command object
     *
     * @param token the Token
     * @return an [ActivateAccount] command object
     */
    override fun activateAccount(token: String): Command<String, Multimap<ErrorTag, String>> {
        return ActivateAccount(
                token = token,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        )
    }
}