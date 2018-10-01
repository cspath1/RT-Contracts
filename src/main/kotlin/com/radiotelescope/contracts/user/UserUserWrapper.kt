package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.UserContext
import com.radiotelescope.security.UserContextWrapper
import com.radiotelescope.security.UserPreconditionFailure

/**
 * Concrete implementation of the [UserContextWrapper] that takes a [UserFactory]
 * and will return a factory that handles all user role validation
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
) : UserContextWrapper<UserFactory> {
    /**
     * Override of the factory method that creates a [UserFactory] object that will
     * handle roles and permission checking
     */
    override fun factory(userPreconditionFailure: UserPreconditionFailure): UserFactory {
        return object : UserFactory {
            /**
             * No Role validation is required to create a new user, so just
             * return the regular Register object
             */
            override fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
                return Register(
                        request = request,
                        userRepo = userRepo,
                        userRoleRepo = userRoleRepo
                )
            }
        }
    }
}