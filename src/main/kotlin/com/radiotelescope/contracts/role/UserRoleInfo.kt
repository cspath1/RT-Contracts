package com.radiotelescope.contracts.role

import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.UserRole

/**
 * Data class representing a read-only model of the
 * [UserRole] Entity, with some additionally [UserInfo]
 * information for the admin viewing it on the client-side
 *
 * @param id the [UserRole] id
 * @param role the [UserRole] role
 * @param userInfo the [UserInfo] data class
 */
data class UserRoleInfo(
        val id: Long,
        val role: UserRole.Role,
        val userInfo: UserInfo
) {

    /**
     * Secondary constructor that takes a [UserRole] object and
     * [UserInfo] object to set all fields
     */
    constructor(userRole: UserRole, userInfo: UserInfo) : this(
            id = userRole.id,
            role = userRole.role,
            userInfo = userInfo
    )
}