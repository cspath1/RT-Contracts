package com.radiotelescope.contracts.allottedTimeCap

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private var userId = -1L

    @Before
    fun setup(){
        val user = testUtil.createUser("lferree@ycp.edu")
        userId = user.id

        // Make the user a Guest
        testUtil.createUserRoleForUser(
                user = user,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create an AllottedTimeCap, as normally it would be set via their role
        testUtil.createAllottedTimeCapForUser(
                user = user,
                allottedTime = 1L
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        userId = userId,
                        allottedTime = 5 * 60 * 60 * 1000
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        // Should not have failed
        Assert.assertNull(error)
        Assert.assertNotNull(id)
    }

    @Test
    fun testInvalidUserId_Failure(){
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        userId = -1L,
                        allottedTime = 1L
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        Assert.assertNull(id)
        Assert.assertNotNull(error)
        Assert.assertTrue(error!![ErrorTag.USER_ID].isNotEmpty())
    }

    @Test
    fun testUserRole_NotApproved_Failure(){
        // Create a new user
        val user = testUtil.createUser("lferree@ycp.edu2")
        val theUserId = user.id

        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        userId = theUserId,
                        allottedTime = 1L
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        Assert.assertNull(id)
        Assert.assertNotNull(error)
        Assert.assertTrue(error!![ErrorTag.CATEGORY_OF_SERVICE].isNotEmpty())
    }

    @Test
    fun testInvalidAllottedTime_Failure(){
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        userId = userId,
                        allottedTime = -1L
                ),
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()

        Assert.assertNull(id)
        Assert.assertNotNull(error)
        Assert.assertTrue(error!![ErrorTag.ALLOTTED_TIME].isNotEmpty())
    }
}