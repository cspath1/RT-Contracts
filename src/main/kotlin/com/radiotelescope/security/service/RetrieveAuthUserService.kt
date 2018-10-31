package com.radiotelescope.security.service

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.ErrorTag
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
        private val userRepo: IUserRepository
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

        val authorities = arrayListOf<SimpleGrantedAuthority>()

        authToken.authorities.forEach {
            authorities.add(SimpleGrantedAuthority(it.authority))
        }

        val user = userRepo.findById(authToken.userId!!).get()

        validateStatus(user.status)?.let { return SimpleResult(null, it) }

        val userSession = UserSession(
                userId = user.id,
                email = user.email,
                roles = authorities,
                firstName = user.firstName,
                lastName = user.lastName
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
    fun validateStatus(status: User.Status): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()
        when (status) {
            User.Status.Deleted -> errors.put(ErrorTag.STATUS, "User was not found")
            User.Status.Banned -> errors.put(ErrorTag.STATUS, "User is currently banned")
            User.Status.Inactive -> errors.put(ErrorTag.STATUS, "User is not currently activated")
            User.Status.Active -> return null
        }

        return errors
    }
}