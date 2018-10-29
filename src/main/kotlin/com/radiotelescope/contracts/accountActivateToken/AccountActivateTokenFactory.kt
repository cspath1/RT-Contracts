package com.radiotelescope.contracts.accountActivateToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.accountActivateToken.AccountActivateToken

/**
 * Abstract factory interface with methods for all [AccountActivateToken]
 * command objects
 */
interface AccountActivateTokenFactory {
    /**
     * Abstract command used to activate an account
     *
     * @param token the Token
     * @return a [Command] object
     */
    fun activateAccount(token: String): Command<String, Multimap<ErrorTag, String>>
}