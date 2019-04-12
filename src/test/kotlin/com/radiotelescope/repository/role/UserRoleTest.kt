package com.radiotelescope.repository.role

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.user.User
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
internal class UserRoleTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val nonApprovedRoles = arrayListOf<UserRole>()

    private lateinit var firstUser: User

    @Before
    fun setUp() {
        // Create two users and roles for those users
        firstUser = testUtil.createUser("cspath1@ycp.edu")
        val secondUser = testUtil.createUser("spathcody@gmail.com")

        val firstRoles = testUtil.createUserRolesForUser(
                user = firstUser,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )
        val secondRoles = testUtil.createUserRolesForUser(
                user = secondUser,
                role = UserRole.Role.MEMBER,
                isApproved = false
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
    fun testFindNonApprovedRoles() {
        val rolePage = userRoleRepo.findNeedsApprovedUserRoles(PageRequest.of(0, 5))

        assertEquals(2, rolePage.content.size)

        // Make sure each is role is actually not approved
        rolePage.content.forEach {
            assertFalse(it.approved)

            // also make sure it is one of the ones from the list
            // populated during setup
            val sameRole = nonApprovedRoles.any { role ->
                role.id == it.id
            }

            assertTrue(sameRole)
        }
    }

    @Test
    fun testFindMembershipRole() {
        val roles = userRoleRepo.findAllByUserId(firstUser.id)

        roles.forEach {
            it.approved = true
            userRoleRepo.save(it)
        }

        val role = userRoleRepo.findMembershipRoleByUserId(firstUser.id)

        assertNotNull(role)
        assertTrue(role!!.approved)
        assertEquals(UserRole.Role.STUDENT, role.role)
    }

    @Test
    fun testFindAllApprovedRolesByUserId() {
        val user = testUtil.createUser("realtadukoo@gmail.com")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )

        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )

        val roles = userRoleRepo.findAllApprovedRolesByUserId(user.id)

        roles.forEach {
            assertTrue(it.approved)
        }
        assertEquals(2, roles.size)
    }
}