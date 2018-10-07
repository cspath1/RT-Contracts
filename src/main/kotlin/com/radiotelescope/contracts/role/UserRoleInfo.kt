package com.radiotelescope.contracts.role

import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.UserRole

data class UserRoleInfo(
        val id: Long,
        val role: UserRole.Role,
        val userInfo: UserInfo
) {

    constructor(userRole: UserRole, userInfo: UserInfo) : this(
            id = userRole.id,
            role = userRole.role,
            userInfo = userInfo
    )
}