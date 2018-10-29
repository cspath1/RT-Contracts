package com.radiotelescope.contracts.accountActivateToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [AccountActivateTokenFactory] and is responsible for
 * all user role validation for the AccountActivateToken Entity
 *
 * @property factory the [AccountActivateTokenFactory] interface
 */
class UserAccountActivateTokenWrapper(
        private val factory: AccountActivateTokenFactory
) {
    /**
     * Activate Account method that will return calls the [AccountActivateTokenFactory.activateAccount]
     * command object. This does not need any user role authentication since the user will
     * not be logged in at the time
     */
    fun activateAccount(token: String): Command<Long, Multimap<ErrorTag, String>> {
        return factory.activateAccount(token)
    }
}