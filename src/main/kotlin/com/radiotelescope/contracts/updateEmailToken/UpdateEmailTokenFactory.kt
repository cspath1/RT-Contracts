package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Abstract factory interface with methods for all [UpdateEmailToken]
 * Command objects
 */
interface UpdateEmailTokenFactory {
    /**
     * Abstract command use to create token for resetting user password
     *
     * @param request the [CreateUpdateEmailToken.Request]
     * @return a [Command] object
     */
    fun requestUpdateEmail(request: CreateUpdateEmailToken.Request) : Command<String, Multimap<ErrorTag, String>>

}