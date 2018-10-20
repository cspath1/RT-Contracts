package com.radiotelescope.contracts.role

import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

internal class UserRoleInfoTest {
    private lateinit var userInfo: UserInfo

    @Before
    fun setUp() {
        val user = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "Password"
        )

        user.phoneNumber = "717-823-2216"
        user.company = "York College of PA"
        user.active = true
        user.status = User.Status.Active
        user.id = 1L

        userInfo = UserInfo(
                user = user,
                userRole = UserRole.Role.GUEST
        )
    }

    @Test
    fun testPrimaryConstructor() {
        val userRoleInfo = UserRoleInfo(
                id = 1L,
                role = UserRole.Role.GUEST,
                userInfo = userInfo
        )

        assertEquals(1L, userRoleInfo.id)
        assertEquals(UserRole.Role.GUEST, userRoleInfo.role)
        assertEquals(userInfo, userRoleInfo.userInfo)
    }

    @Test
    fun testSecondaryConstructor() {
        val userRole = UserRole(
                userId = userInfo.id,
                role = UserRole.Role.GUEST
        )

        userRole.id = 1L

        val userRoleInfo = UserRoleInfo(
                userRole = userRole,
                userInfo = userInfo
        )

        assertEquals(userRole.id, userRoleInfo.id)
        assertEquals(userRole.role, userRoleInfo.role)
        assertEquals(userInfo, userRoleInfo.userInfo)
    }
}