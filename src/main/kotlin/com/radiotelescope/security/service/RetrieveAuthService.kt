package com.radiotelescope.security.service

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.AuthenticatedUserToken
import com.radiotelescope.security.UserSession
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class RetrieveAuthService(
        private val userRepo: IUserRepository
) {
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

        val userSession = UserSession(
                userId = user.id,
                email = user.email,
                roles = authorities,
                firstName = user.firstName,
                lastName = user.lastName
        )

        return SimpleResult(userSession, null)
    }
}