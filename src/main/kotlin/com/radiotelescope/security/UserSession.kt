package com.radiotelescope.security

import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Data class containing pertinent user information
 *
 * @param userId the User's id
 * @param email the User's email
 * @param roles the User's roles
 * @param firstName the User's first name
 * @param lastName the User's last name
 */
data class UserSession(
        var userId: Long,
        var email: String,
        var roles: Collection<SimpleGrantedAuthority>,
        var firstName: String,
        var lastName: String
)