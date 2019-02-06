package com.radiotelescope.security

import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.service.UserDetailsImpl
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
        private var userRepo: IUserRepository,
        private var userRoleRepo: IUserRoleRepository
) : AuthenticationProvider {
    /**
     * Performs the user authentication using Spring Security
     */
    override fun authenticate(authentication: Authentication?): Authentication {
        val context = SecurityContextHolder.getContext()

        if (context.authentication != null && authentication?.name == context.authentication.name)
            return context.authentication

        val userDetails = userDetailsService.loadUserByUsername(authentication?.name) as UserDetailsImpl

        val verified = execute(
                email = userDetails.username,
                password = authentication!!.credentials.toString()
        )

        if (!verified)
            throw AuthenticationCredentialsNotFoundException("Invalid Email or Password")

        System.out.println("Logging user in.")

        return AuthenticatedUserToken(
                userId = userDetails.id,
                authorities = userDetails.authorities,
                password = userDetails.password,
                email = userDetails.username
        )
    }

    /**
     * State that the AuthenticationProviderImpl supports anything that
     * extends the [UsernamePasswordAuthenticationToken] class
     */
    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    /**
     * Function that will call the [Authenticate] command, which will
     * verify if the email and password matches with a record in the
     * database, signifying a successful login
     *
     * @param email the email
     * @param password the password
     * @return true or false, based on if the user was authenticated
     */
    private fun execute(email: String, password: String): Boolean {
        val simpleResult = Authenticate(
                request = Authenticate.Request(
                        email = email,
                        password = password
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        return simpleResult.success != null
    }
}