package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.UserContext

/**
 * Wrapper that takes a [UserFactory] and is responsible for all
 * user role validations for our endpoints for the User Entity
 * @property context the [UserContext] interface
 * @property factory the [UserFactory] factory interface
 * @property userRepo the [IUserRepository] interface
 * @property userRoleRepo the [IUserRoleRepository] interface
 */
class UserUserWrapper(
        private val context: UserContext,
        private val factory: UserFactory,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) {
    fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Authenticate(
                request = request,
                userRepo = userRepo
        )
    }
}