package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command

/**
 * Wrapper that takes a [ResetPasswordTokenFactory] and is responsible for all
 * user role validations for endpoints for the ResetPasswordToken Entity
 *
 * @property resetPasswordTokenFactory the [ResetPasswordTokenFactory] interface
 */
class UserResetPasswordTokenWrapper (
        private val resetPasswordTokenFactory: ResetPasswordTokenFactory
) {

    /**
     * Reset Password Token function that will return a [CreateResetPasswordToken] command object.
     * This does not need any user role authentication since the user will not be signed in at the time
     *
     * @param email the User email
     * @return a [CreateResetPasswordToken] command object
     */
    fun requestPasswordReset(email: String): Command<String, Multimap<ErrorTag, String>> {
        return resetPasswordTokenFactory.requestPasswordReset(email)
    }

    /**
     * Reset Password function that will return a [ResetPassword] command object.
     * This does not need any user role authentication since the user will not be signed in a the time
     *
     * @param request the [ResetPassword.Request]
     * @param token the reset password token
     * @return a [ResetPassword] command object
     */
    fun resetPassword(request: ResetPassword.Request, token: String) : Command<Long, Multimap<ErrorTag, String>> {
        return resetPasswordTokenFactory.resetPassword(
                request = request,
                token = token
        )
    }
}