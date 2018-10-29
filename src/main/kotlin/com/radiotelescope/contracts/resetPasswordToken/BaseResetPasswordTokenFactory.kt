package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User


/**
 * Base concrete implementation of the [ResetPasswordTokenFactory] interface
 *
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class BaseResetPasswordTokenFactory (
        private val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        private val userRepo: IUserRepository
) : ResetPasswordTokenFactory {

    /**
     * Override of the [ResetPasswordTokenFactory.requestPasswordReset] method that will return a
     * [CreateResetPasswordToken] command object
     *
     * @param email the [User] email
     * @return a [CreateResetPasswordToken] command object
     */
    override fun requestPasswordReset(email: String): Command<String, Multimap<ErrorTag, String>> {
        return CreateResetPasswordToken(
                email = email,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [ResetPasswordTokenFactory.resetPassword] method that will return a
     * [ResetPassword] command object
     *
     * @param request the [ResetPassword.Request]
     * @param token the ResetPasswordToken token
     * @return a [ResetPassword] command object
     */
    override fun resetPassword(request: ResetPassword.Request, token: String): Command<Long, Multimap<ErrorTag, String>> {
        return ResetPassword(
                request = request,
                token = token,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        )
    }
}