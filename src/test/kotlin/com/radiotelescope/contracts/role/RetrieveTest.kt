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
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private var roleId: Long? = null

    @Before
    fun setUp() {
        // Create a user and some roles and timecap
        val user = testUtil.createUser("cspath1@ycp.edu")
        val roles = testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = Appointment.STUDENT_APPOINTMENT_TIME_CAP
        )

        roles.forEach {
            if (!it.approved)
                roleId = it.id
        }
    }

    @Test
    fun testValidConstraints_Success() {
        val (info, errors) = Retrieve(
                roleId = roleId!!,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(info)
        assertNull(errors)

        assertEquals(info!!.id, roleId!!)
    }

    @Test
    fun testNonExistentRole_Failure() {
        val (info, errors) = Retrieve(
                roleId = 311L,
                userRoleRepo = userRoleRepo,
                userRepo = userRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        assertNotNull(errors)
        assertNull(info)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }
}
