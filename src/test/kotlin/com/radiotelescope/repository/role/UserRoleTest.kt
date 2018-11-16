package com.radiotelescope.repository.role

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserRoleTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val nonApprovedRoles = arrayListOf<UserRole>()

    private lateinit var firstUser: User

    @Before
    fun setUp() {
        // Create two users and roles for those users
        firstUser = testUtil.createUser(
                email = "cspath1@ycp.edu",
                accountHash = "Test Account 1"
        )
        val secondUser = testUtil.createUser(
                email = "spathcody@gmail.com",
                accountHash = "Test Account 2"
        )

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
}