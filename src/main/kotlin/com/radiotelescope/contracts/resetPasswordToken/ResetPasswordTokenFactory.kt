package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

interface ResetPasswordTokenFactory {

    /**
     * Abstract command used to create token for resetting user password
     *
     * @param email the user email
     * @return a [Command] object
     */
    fun resetPasswordToken(email: String) : Command<String, Multimap<ErrorTag, String>>
}