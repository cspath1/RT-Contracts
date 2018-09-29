package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository

class BaseUserFactory(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : UserFactory {
    override fun register(request: Register.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }
}