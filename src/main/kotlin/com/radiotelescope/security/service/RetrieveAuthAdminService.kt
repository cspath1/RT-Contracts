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

        // Use only approved roles
        val roles = userRoleRepo.findAllByUserId(authToken.userId!!).filter { it.approved }

        val isAdmin = roles.any {
            it.role == UserRole.Role.ADMIN
        }

        if (!isAdmin) {
            errors.put(ErrorTag.ROLES, "User is not an admin")

            return SimpleResult(null, errors)
        }

        val authorities = getAuthorities(userId = authToken.userId)

        val user = userRepo.findById(authToken.userId).get()

        val userSession = UserSession(
                userId = authToken.userId,
                email = user.email,
                roles = authorities,
                lastName = user.lastName,
                firstName = user.firstName,
                accountActive = user.active
        )

        return SimpleResult(userSession, null)
    }

    /**
     * Retrieves a user's roles and adapts them into [SimpleGrantedAuthority] objects
     *
     * @param userId the User id
     * @return a list of [SimpleGrantedAuthority] objects
     */
    private fun getAuthorities(userId: Long): List<SimpleGrantedAuthority> {
        val roles = userRoleRepo.findAllByUserId(userId)
        val authorities = arrayListOf<SimpleGrantedAuthority>()

        roles.forEach {
            if (it.approved)
                authorities.add(SimpleGrantedAuthority("ROLE_${it.role.name.toUpperCase()}"))
        }

        return authorities
    }
}