package com.radiotelescope.security

import com.radiotelescope.contracts.user.Authenticate
import com.radiotelescope.controller.model.ses.SesSendForm
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.service.UserDetailsImpl
import com.radiotelescope.security.service.UserDetailsServiceImpl
import com.radiotelescope.service.ses.IAwsSesSendService
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
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 */
@Component
class AuthenticationProviderImpl(
        @Qualifier("UserDetailsService")
        private var userDetailsService: UserDetailsService,
        private var userRepo: IUserRepository,
        private var userRoleRepo: IUserRoleRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val loginAttemptRepo: ILoginAttemptRepository,
        private val awsSesSendService: IAwsSesSendService
) : AuthenticationProvider {
    /**
     * Performs the user authentication using Spring Security
     *
     * @param authentication the [AuthenticatedUserToken] (or null if the user is not authenticated)
     * @return an [AuthenticatedUserToken] object
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
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        ).execute()

        // If the Authenticate command failed, and it was the fifth failed login
        // attempt, send an email to the user
        if (simpleResult.error != null) {
            val failedLoginAttemptList = loginAttemptRepo.findByUser_Email(email)

            // NOTE: This will return an empty list if the email entered
            // is not associated with a user. Also, we only want to send out
            // one email when the account reaches the threshold of being "locked"
            if (failedLoginAttemptList.size == 5) {
                sendEmail(email)
            }
        }

        return simpleResult.success != null
    }

    private fun sendEmail(email: String) {
        val sendForm = SesSendForm(
                toAddresses = listOf(email),
                fromAddress = "YCAS Radio Telescope <cspath1@ycp.edu>",
                subject = "Account Locked",
                htmlBody = "<p>Due to consecutive failed login attempts, your account has been locked</p>" +
                        "<p>Please reset your password in order to unlock it.</p>"
        )

        awsSesSendService.execute(sendForm)
    }
}