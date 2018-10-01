package com.radiotelescope.security

import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

/**
 * Concrete implementation of the [AuthenticationProvider]
 *
 * @param userDetailsService the [UserDetailsServiceImpl] service
 * @param userRepo the [IUserRepository]
 */
@Component
class AuthenticationProviderImpl(
        @Qualifier("UserDetailsService")
        private var userDetailsService: UserDetailsService,
        private var userRepo: IUserRepository
) : AuthenticationProvider {
    /**
     * Performs the user authentication using Spring Security
     */
    override fun authenticate(authentication: Authentication?): Authentication {
        val context = SecurityContextHolder.getContext()

        if (context.authentication != null && authentication?.name == context.authentication.name)
            return context.authentication
        else
            // TODO - Remove this else statement once authentication service is implemented
            throw AuthenticationCredentialsNotFoundException("Invalid Email or Password")

        // TODO - Add authentication service here once implemented


    }

    /**
     * State that the AuthenticationProviderImpl supports anything that
     * extends the [UsernamePasswordAuthenticationToken] class
     */
    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

}