package com.radiotelescope.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * User Token that is created by Spring Security upon login
 *
 * @param email the User's email
 * @param password the User's password
 * @param authorities a [Collection] of the User's authorities
 * @param userId the User's id
 */
class AuthenticatedUserToken(
        email: String,
        password: String,
        authorities: Collection<GrantedAuthority>,
        val userId: Long?
) : UsernamePasswordAuthenticationToken(email, password, authorities)