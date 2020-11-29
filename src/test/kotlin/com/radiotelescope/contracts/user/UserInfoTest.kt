package com.radiotelescope.contracts.user

import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test

internal class UserInfoTest {
    @Test
    fun testPrimaryConstructor() {
        val userInfo = UserInfo(
                id = 1L,
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                company = "York College of PA",
                phoneNumber = "717-823-2216",
                profilePicture = "dummy image",
                profilePictureApproved = true,
                active = true,
                status = User.Status.ACTIVE.label,
                notificationType = User.NotificationType.PUSHNOTIFICATION,
                firebaseID = "abc123",
                membershipRole = UserRole.Role.GUEST.label,
                allottedTime = 100L
        )

        assertEquals("Cody", userInfo.firstName)
        assertEquals("Spath", userInfo.lastName)
        assertEquals("cspath1@ycp.edu", userInfo.email)
        assertEquals(1L, userInfo.id)
        assertEquals("York College of PA", userInfo.company)
        assertEquals("717-823-2216", userInfo.phoneNumber)
        assertTrue(userInfo.active)
        assertEquals(User.NotificationType.PUSHNOTIFICATION, userInfo.notificationType)
        assertEquals("abc123", userInfo.firebaseID)
        assertEquals(User.Status.ACTIVE.label, userInfo.status)
        assertEquals(UserRole.Role.GUEST.label, userInfo.membershipRole)
        assertEquals(100L, userInfo.allottedTime)
    }

    @Test
    fun testSecondaryConstructor() {
        val user = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "Password"
        )

        user.phoneNumber = "717-823-2216"
        user.company = "York College of PA"
        user.active = true
        user.status = User.Status.ACTIVE
        user.id = 1L

        val userInfo = UserInfo(user, UserRole.Role.GUEST.label, 500L)

        assertEquals("Cody", userInfo.firstName)
        assertEquals("Spath", userInfo.lastName)
        assertEquals("cspath1@ycp.edu", userInfo.email)
        assertEquals(1L, userInfo.id)
        assertEquals("York College of PA", userInfo.company)
        assertEquals("717-823-2216", userInfo.phoneNumber)
        assertTrue(userInfo.active)
        assertEquals(User.Status.ACTIVE.label, userInfo.status)
        assertEquals(UserRole.Role.GUEST.label, userInfo.membershipRole)
        assertEquals(500L, userInfo.allottedTime)
    }
}