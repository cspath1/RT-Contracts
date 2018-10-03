package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository

/**
 * Base concrete implementation of the [UserFactory] interface
 */
class BaseUserFactory(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : UserFactory {
    /**
     * Override of the [UserFactory.register] method that will return a [Register] command object
     */
    override fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    override fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Authenticate(
                request = request,
                userRepo = userRepo
        )
    }
}