package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private var pageable = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Create a few users with timecaps
        val user1 = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUserRolesForUser(user1, UserRole.Role.MEMBER, true)
        testUtil.createAllottedTimeCapForUser(user1, Appointment.MEMBER_APPOINTMENT_TIME_CAP)

        val user2 = testUtil.createUser("spathcody@gmail.com")
        testUtil.createUserRolesForUser(user2, UserRole.Role.RESEARCHER, true)
        testUtil.createAllottedTimeCapForUser(user2, null)

        val user3 = testUtil.createUser("codyspath@gmail.com")
        testUtil.createUserRolesForUser(user3, UserRole.Role.GUEST, true)
        testUtil.createAllottedTimeCapForUser(user3, Appointment.GUEST_APPOINTMENT_TIME_CAP)
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        Assert.assertNull(errors)
        Assert.assertNotNull(page)
        Assert.assertEquals(3, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        allottedTimeCapRepo.deleteAll()
        userRoleRepo.deleteAll()
        userRepo.deleteAll()

        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        Assert.assertNotNull(page)
        Assert.assertNull(errors)
        Assert.assertEquals(0, page!!.content.size)
    }
}