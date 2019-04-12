package com.radiotelescope.contracts.role

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UnapprovedListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private val nonApprovedRoles = arrayListOf<UserRole>()

    @Before
    fun setUp() {
        // Create two users and roles for those users
        val firstUser = testUtil.createUser("cspath1@ycp.edu")
        val secondUser = testUtil.createUser("spathcody@gmail.com")

        val firstRoles = testUtil.createUserRolesForUser(
                user = firstUser,
                role = UserRole.Role.RESEARCHER,
                isApproved = false
        )
        testUtil.createAllottedTimeCapForUser(
                user = firstUser,
                allottedTime = null
        )

        val secondRoles = testUtil.createUserRolesForUser(
                user = secondUser,
                role = UserRole.Role.MEMBER,
                isApproved = false
        )
        testUtil.createAllottedTimeCapForUser(
                user = secondUser,
                allottedTime = Appointment.MEMBER_APPOINTMENT_TIME_CAP
        )

        firstRoles.forEach {
            if (!it.approved)
                nonApprovedRoles.add(it)
        }

        secondRoles.forEach {
            if (!it.approved)
                nonApprovedRoles.add(it)
        }

        // There should be two non-approved roles
        assertEquals(2, nonApprovedRoles.size)
    }

    @Test
    fun testUnApprovedList_Success() {
        val (infos, errors) = UnapprovedList(
                pageable = PageRequest.of(0, 5),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(infos)
        assertNull(errors)

        infos!!.forEach {

            val sameRole = nonApprovedRoles.any { role ->
                role.id == it.id
            }

            assertTrue(sameRole)
        }
    }
}