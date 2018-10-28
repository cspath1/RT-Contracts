package com.radiotelescope.contracts.resetPasswordToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User


/**
 * Base concrete implementation of the [ResetPasswordTokenFactory] interface
 *
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository]
 * @param userRepo the [IUserRepository]
 */
class BaseResetPasswordTokenFactory (
        private val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        private val userRepo: IUserRepository
) : ResetPasswordTokenFactory {

    /**
     * Override of the [ResetPasswordTokenFactory.resetPasswordToken] method that will return a
     * [CreateResetPasswordToken] command object
     *
     * @param userId the [User] id
     * @return a [CreateResetPasswordToken] command object
     */
    override fun resetPasswordToken(userId: Long): Command<String, Multimap<ErrorTag, String>> {
        return CreateResetPasswordToken(
                userId = userId,
                resetPasswordTokenRepo = resetPasswordTokenRepo,
                userRepo = userRepo
        )
    }
}