package com.radiotelescope.contracts.updateEmailToken

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Base concrete implementation of the [UpdateEmailTokenFactory] interface
 *
 * @param updateEmailTokenRepo the [IUpdateEmailTokenRepository] interface
 * @param userRepo the [IUserRepository] interface
 */
class BaseUpdateEmailTokenFactory (
        private val updateEmailTokenRepo: IUpdateEmailTokenRepository,
        private val userRepo: IUserRepository
) : UpdateEmailTokenFactory {

    /**
     * Override of the [UpdateEmailTokenFactory.requestUpdateEmail] method that will return a
     * [CreateUpdateEmailToken] command object
     *
     * @param request the [CreateUpdateEmailToken.Request]
     * @return a [CreateUpdateEmailToken] command object
     */
    override fun requestUpdateEmail(request: CreateUpdateEmailToken.Request): Command<String, Multimap<ErrorTag, String>> {
        return CreateUpdateEmailToken(
                request = request,
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UpdateEmailTokenFactory.updateEmail] method that will return a
     * [UpdateEmail] command object
     *
     * @param token the token for updating user email
     * @return a [CreateUpdateEmailToken] command object
     */
    override fun updateEmail(token: String): Command<Long, Multimap<ErrorTag, String>> {
        return UpdateEmail(
                token = token,
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        )
    }
}