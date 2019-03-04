package com.radiotelescope.contracts.role

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UnapprovedListTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

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
    fun testUnApprovedList_Success() {
        val (infos, errors) = UnapprovedList(
                pageable = PageRequest.of(0, 5),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
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