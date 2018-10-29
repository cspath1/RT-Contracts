package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken

/**
 * Abstract factory interface with methods for all [ResetPasswordToken]
 * Command objects
 */
interface ResetPasswordTokenFactory {
    /**
     * Abstract command use to create token for resetting user password
     *
     * @param email the user email
     * @return a [Command] object
     */
    fun requestPasswordReset(email: String) : Command<String, Multimap<ErrorTag, String>>

    /**
     * Abstract command use to resetPassword for a user
     *
     * @param request the [ResetPassword.Request]
     * @param token the ResetPasswordToken token
     * @return a [Command] object
     */
    fun resetPassword(request: ResetPassword.Request, token: String) : Command<Long, Multimap<ErrorTag, String>>
}