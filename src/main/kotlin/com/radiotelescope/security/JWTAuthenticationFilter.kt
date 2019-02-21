package com.radiotelescope.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.radiotelescope.config.JWTConfiguration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Extension of the [UsernamePasswordAuthenticationFilter] that handles the JSON Web Token
 * used to identify users. Acts as a wrapper around the login process that, upon successful
 * authentication will create and return a JSON Web Token to the client.
 */
class JWTAuthenticationFilter : UsernamePasswordAuthenticationFilter {
    private var jwtConfiguration: JWTConfiguration

    /**
     * Secondary constructor that takes an authentication manager object
     * and sets the filter's authentication manager object to this parameter.
     * It also takes a [JWTConfiguration] object and sets the private variable
     * to this.
     *
     * @param authenticationManager the [AuthenticationManager]
     * @param jwtConfiguration the [JWTConfiguration]
     */
    constructor(
            authenticationManager: AuthenticationManager,
            jwtConfiguration: JWTConfiguration
    ) : super() {
        this.authenticationManager = authenticationManager
        this.jwtConfiguration = jwtConfiguration
    }

    /**
     * Override of the [UsernamePasswordAuthenticationFilter.obtainUsername] method
     * that will look for a request parameter with a name of "email" since this is what
     * is used for user authentication
     *
     * @param request the [HttpServletRequest]
     * @return the email addressed entered by the user
     */
    override fun obtainUsername(request: HttpServletRequest?): String {
        return request?.getParameter("email") ?: ""
    }

    /**
     * Override of the [UsernamePasswordAuthenticationFilter.successfulAuthentication] method that
     * will create a JSON Web token and return it to the client in the response headers
     *
     * @param request the [HttpServletRequest]
     * @param response the [HttpServletResponse]
     * @param chain the [FilterChain]
     * @param authResult the [AuthenticatedUserToken]
     */
    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        authResult as AuthenticatedUserToken
        val token = JWT.create()
                .withSubject(authResult.email)
                .withExpiresAt(Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7)))
                .sign(Algorithm.HMAC512(jwtConfiguration.secretKey().toByteArray()))
        response?.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
    }
}