package com.radiotelescope.security.service

import com.radiotelescope.repository.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Concrete implementation of [UserDetails] class
 *
 * @param user a [User] entity
 * @param grantedAuthorities a [Set] of the User's roles
 * @property id the User id
 * @property active the User's active flag
 * @property email the User's email
 * @property password the User's password
 */
class UserDetailsImpl(
        user: User,
        private var grantedAuthorities: Set<GrantedAuthority>
) : UserDetails {
    var id = user.id
    private var active = user.active
    private var email = user.email
    private var password = user.password

    /**
     * Return the list of granted authorities
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return grantedAuthorities.toMutableList()
    }

    /**
     * Credentials do not expire so return false
     */
    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    /**
     * Return the User's active field
     */
    override fun isAccountNonLocked(): Boolean {
        return active
    }

    /**
     * Accounts do not expire so return false
     */
    override fun isAccountNonExpired(): Boolean {
        return false
    }

    /**
     * return the User's email
     */
    override fun getUsername(): String {
        return email
    }

    /**
     * Return the User's active field
     */
    override fun isEnabled(): Boolean {
        return active
    }

    /**
     * Return the User's password
     */
    override fun getPassword(): String {
        return password
    }
}