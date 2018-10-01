package com.radiotelescope.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Concrete implementation of the [AuthenticationSuccessHandler] interface
 */
class LoginHandlerImpl : AuthenticationSuccessHandler {
    /**
     * Override of the [AuthenticationSuccessHandler.onAuthenticationSuccess] method
     * that sets the session to be 10 days
     */
    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        // Set the timeout to 10 days
        request?.session?.maxInactiveInterval = 60 * 60 * 24 * 10
    }
}