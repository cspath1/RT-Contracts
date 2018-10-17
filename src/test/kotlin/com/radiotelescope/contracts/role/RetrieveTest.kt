package com.radiotelescope.contracts.role

import com.radiotelescope.BaseDataJpaTest
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class RetrieveTest {
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

    private var roleId: Long? = null

    @Before
    fun setUp() {
        // Create a user and some roles
        val user = testUtil.createUser("cspath1@ycp.edu")
        val roles = testUtil.createUserRolesForUser(
                userId = user.id,
                role = UserRole.Role.STUDENT,
                isApproved = false
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
                userRepo = userRepo
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
                userRepo = userRepo
        ).execute()

        assertNotNull(errors)
        assertNull(info)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }
}
