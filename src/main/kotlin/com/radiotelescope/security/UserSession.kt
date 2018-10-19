package com.radiotelescope.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserSession(
        var userId: Long,
        var email: String,
        var roles: Collection<SimpleGrantedAuthority>,
        var firstName: String,
        var lastName: String
)