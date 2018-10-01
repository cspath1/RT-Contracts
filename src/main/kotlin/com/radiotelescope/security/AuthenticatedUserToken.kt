package com.radiotelescope.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * User Token that is created by Spring Security upon login
 */
class AuthenticatedUserToken(
        email: String,
        password: String,
        authorities: Collection<GrantedAuthority>,
        val userId: Long?
) : UsernamePasswordAuthenticationToken(email, password, authorities)