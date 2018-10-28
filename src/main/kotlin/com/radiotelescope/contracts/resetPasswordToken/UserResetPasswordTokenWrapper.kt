package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.user.IUserRepository

/**
 * Wrapper that takes a [ResetPasswordTokenFactory] and is responsible for all
 * user role validations for endpoints for the ResetPasswordToken Entity
 *
 * @property resetPasswordTokenRepo the [IResetPasswordTokenRepository] interface
 * @property userRepo the [IUserRepository] interface
 */
class UserResetPasswordTokenWrapper (
        private val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        private val userRepo: IUserRepository
){

    /**
     * Reset Password Token function that will return a [CreateResetPasswordToken] command object.
     * This does not need any user role authentication since the user will not be signed in a the time
     *
     * @param email the User email
     * @return a [CreateResetPasswordToken] command object
     */
    fun resetPasswordToken(email: String): Command<String, Multimap<ErrorTag, String>> {
        return CreateResetPasswordToken(
                email = email,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        )
    }

    /**
     * Reset Password funtion that will reutnr a [ResetPassword] command object.
     * This does not need any user role authentication since the user will not be signed in a the time
     *
     * @param request the [ResetPassword.Request]
     * @param token the reset password token
     * @return a [ResetPassword] command object
     */
    fun resetPassword(request: ResetPassword.Request, token: String) : Command<Long, Multimap<ErrorTag, String>> {
        return ResetPassword(
                request = request,
                token = token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        )
    }
}