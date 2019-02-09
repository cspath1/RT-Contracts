package com.radiotelescope.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.radiotelescope.config.JWTConfiguration
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(
        authenticationManager: AuthenticationManager,
        private val jwtConfiguration: JWTConfiguration,
        private val userRepo: IUserRepository
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(SecurityConstants.HEADER_STRING)

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        val authUser = getAuthentication(request)

        SecurityContextHolder.getContext().authentication = authUser
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): AuthenticatedUserToken? {
        val token = request.getHeader(SecurityConstants.HEADER_STRING)
        if (token != null) {
            val email = JWT.require(Algorithm.HMAC512(jwtConfiguration.secretKey().toByteArray()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .subject


            if (email != null) {
                val user = userRepo.findByEmail(email)

                if (user != null) {
                    return AuthenticatedUserToken(
                            email = user.email,
                            password = null,
                            authorities = listOf(),
                            userId = user.id
                    )
                }

            }
        }

        return null
    }
}