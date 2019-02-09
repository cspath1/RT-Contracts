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

class JWTAuthenticationFilter : UsernamePasswordAuthenticationFilter {
    private var jwtConfiguration: JWTConfiguration

    constructor(
            authenticationManager: AuthenticationManager,
            jwtConfiguration: JWTConfiguration
    ) : super() {
        this.authenticationManager = authenticationManager
        this.jwtConfiguration = jwtConfiguration
    }

    override fun obtainUsername(request: HttpServletRequest?): String {
        return request?.getParameter("email") ?: ""
    }

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val email = obtainUsername(request)
        val password = obtainPassword(request)

        return authenticationManager.authenticate(AuthenticatedUserToken(
                email = email,
                password = password,
                authorities = listOf(),
                userId = null
        ))
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        authResult as AuthenticatedUserToken
        val token = JWT.create()
                .withSubject(authResult.email)
                .withExpiresAt(Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                .sign(Algorithm.HMAC512(jwtConfiguration.secretKey().toByteArray()))
        response?.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
    }
}