package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
internal class BanTest {

    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private var userId = -1L

    @Before
    fun setUp() {
        // Persist a user
        val theUser = testUtil.createUser("cspath1@ycp.edu")

        userId = theUser.id
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, errors) = Ban(
                id = userId,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The success data type should not be null
        assertNotNull(id)

        // The errors should be though
        assertNull(errors)

        val theUser = userRepo.findById(id!!)

        assertTrue(theUser.isPresent)

        assertEquals(User.Status.Banned, theUser.get().status)
        assertFalse(theUser.get().active)
    }

    @Test
    fun testInvalidUserId_Failure() {
        // Execute the command
        val (id, errors) = Ban(
                id = 311L,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The success data type should be null
        assertNull(id)

        // The errors should not be
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testUserIsAdmin_Failure() {
        // Make the user an admin
        testUtil.createUserRolesForUser(
                userId = userId,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )

        val (id, errors) = Ban(
                id = userId,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The success data type should be null
        assertNull(id)

        // The errors should not be
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ROLES].isNotEmpty())
    }
}