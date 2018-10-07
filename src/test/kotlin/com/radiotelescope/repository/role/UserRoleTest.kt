package com.radiotelescope.repository.role

import com.radiotelescope.BaseDataJpaTest
import com.radiotelescope.TestUtil
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
internal class UserRoleTest : BaseDataJpaTest() {

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val nonApprovedRoles = arrayListOf<UserRole>()

    @Before
    fun setUp() {
        // Create two users and roles for those users
        val firstUser = testUtil.createUser("cspath1@ycp.edu")
        val secondUser = testUtil.createUser("spathcody@gmail.com")

        val firstRoles = testUtil.createUserRolesForUser(
                userId = firstUser.id,
                role = UserRole.Role.STUDENT,
                isApproved = false
        )
        val secondRoles = testUtil.createUserRolesForUser(
                userId = secondUser.id,
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
        rolePage.content.forEach { it ->
            assertFalse(it.approved)

            // also make sure it is one of the ones from the list
            // populated during setup
            val sameRole = nonApprovedRoles.any { role ->
                role.id == it.id
            }

            assertTrue(sameRole)
        }
    }
}