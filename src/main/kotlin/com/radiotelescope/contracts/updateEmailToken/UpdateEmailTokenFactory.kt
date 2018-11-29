package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken

/**
 * Abstract factory interface with methods for all [UpdateEmailToken]
 * Command objects
 */
interface UpdateEmailTokenFactory {
    /**
     * Abstract command use to create token for updating user email
     *
     * @param request the [CreateUpdateEmailToken.Request]
     * @return a [Command] object
     */
    fun requestUpdateEmail(request: CreateUpdateEmailToken.Request) : Command<String, Multimap<ErrorTag, String>>

    /**
     * Abstract command use to  updating user email
     *
     * @param token the token for updating user email
     * @return a [Command] object
     */
    fun updateEmail(token: String): Command<Long, Multimap<ErrorTag, String>>
}