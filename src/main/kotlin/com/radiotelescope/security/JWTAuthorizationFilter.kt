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

/**
 * Extension of the [BasicAuthenticationFilter] that will grab JWT token from
 * the request header and use it to grab the user's context from Spring Security.
 * This will then be used to authenticate requests via role-based authentication
 *
 * @param authenticationManager the [AuthenticationManager] object
 * @param jwtConfiguration the [JWTConfiguration] object
 * @param userRepo the [IUserRepository] interface
 */
class JWTAuthorizationFilter(
        authenticationManager: AuthenticationManager,
        private val jwtConfiguration: JWTConfiguration,
        private val userRepo: IUserRepository
) : BasicAuthenticationFilter(authenticationManager) {
    /**
     * Override of the [BasicAuthenticationFilter.doFilterInternal] method that will grab the
     * JWT toke from the request header so Spring Security knows who is making the request
     *
     * @param request the [HttpServletRequest]
     * @param response the [HttpServletResponse]
     * @param chain the [FilterChain]
     */
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

    /**
     * Private method used to retrieve the [AuthenticatedUserToken] using the
     * JWT Token.
     *
     * @param request the [HttpServletRequest]
     * @return an [AuthenticatedUserToken] or null
     */
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