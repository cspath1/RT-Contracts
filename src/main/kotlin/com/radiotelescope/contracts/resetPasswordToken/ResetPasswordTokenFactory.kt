package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

interface ResetPasswordTokenFactory {

    /**
     * Abstract command used to create token for resetting user password
     *
     * @param userId the user Id
     * @return a [Command] object
     */
    fun resetPasswordToken(userId: Long) : Command<String, Multimap<ErrorTag, String>>
}