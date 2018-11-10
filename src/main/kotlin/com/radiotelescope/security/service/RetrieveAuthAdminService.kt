package com.radiotelescope.security.service

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AuthenticatedUserToken
import com.radiotelescope.security.UserSession
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Service used to determine if a user is an admin
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
@Service
class RetrieveAuthAdminService(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) {
    /**
     * Execute method that will grab the [SecurityContextHolder] authentication token
     * and, if it is not null, will determine if the user is an admin, and if so,
     * return a [UserSession] object. Otherwise, it will return an error.
     */
    fun execute(): SimpleResult<UserSession, Multimap<ErrorTag, String>> {
        val errors = HashMultimap.create<ErrorTag, String>()
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication !is AuthenticatedUserToken) {
            errors.put(ErrorTag.ID, "User is not logged in")

            return SimpleResult(null, errors)
        }

        val authToken: AuthenticatedUserToken = authentication

        val isAdmin = userRoleRepo.findAllByUserId(authToken.userId!!).any {
            it.role == UserRole.Role.ADMIN
        }

        if (!isAdmin) {
            errors.put(ErrorTag.ROLES, "User is not an admin")

            return SimpleResult(null, errors)
        }

        val authorities = arrayListOf<SimpleGrantedAuthority>()

        authToken.authorities.forEach {
            authorities.add(SimpleGrantedAuthority(it.authority))
        }

        val user = userRepo.findById(authToken.userId).get()

        val userSession = UserSession(
                userId = user.id,
                email = user.email,
                roles = authorities,
                firstName = user.firstName,
                lastName = user.lastName,
                accountActive = user.active
        )

        return SimpleResult(userSession, null)
    }
}