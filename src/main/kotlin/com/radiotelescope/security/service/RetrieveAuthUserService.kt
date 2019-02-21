package com.radiotelescope.security.service

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AuthenticatedUserToken
import com.radiotelescope.security.UserSession
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

/**
 * Service used to retrieve user authentication information
 *
 * @param userRepo the [IUserRepository] interface
 */
@Service
class RetrieveAuthUserService(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) {
    /**
     * Execute method that will grab the [SecurityContextHolder] authentication token and,
     * if it is not null. It will then check the user's status to see if they are active,
     * and if so, it will return a [UserSession] object. Otherwise, it will return an error.
     */
    fun execute(): SimpleResult<UserSession, Multimap<ErrorTag, String>> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication !is AuthenticatedUserToken) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "User is not logged in")

            return SimpleResult(null, errors)
        }

        val authToken: AuthenticatedUserToken = authentication

        val user = userRepo.findById(authToken.userId!!).get()

        val authorities = getAuthorities(authToken.userId)

        validateStatus(user.status)?.let { return SimpleResult(null, it) }

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

    /**
     * Since a user should not be validated unless they are active,
     * this method checks the user's status to see if they are active
     * or not, and creates errors accordingly
     *
     * @param status the [User.Status]
     * @return a [Multimap] of errors or null
     */
    private fun validateStatus(status: User.Status): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        when (status) {
            User.Status.DELETED -> errors.put(ErrorTag.STATUS, "Account has been deleted")
            User.Status.BANNED -> errors.put(ErrorTag.STATUS, "Account is currently banned")
            User.Status.INACTIVE -> errors.put(ErrorTag.STATUS, "Account is not currently activated")
            User.Status.ACTIVE -> return null
        }

        return errors
    }

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