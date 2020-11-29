package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateProfilePictureTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var user: User

    @Before
    fun init() {
        user = testUtil.createUser("jhorne@ycp.edu")
    }

    @Test
    fun testSuccessValidConstraints() {
        // Execute the command
        val (id, error) = UpdateProfilePicture(
                request = UpdateProfilePicture.Request(
                        id = user.id,
                        profilePicture = "profile.jpg",
                        profilePictureApproved = true
                ),
                userRepo = userRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)
    }

    @Test
    fun testFailureNonexistantUser() {
        // Execute the command
        val (id, error) = UpdateProfilePicture(
                request = UpdateProfilePicture.Request(
                        id = -1L,
                        profilePicture = "profile.jpg",
                        profilePictureApproved = true
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
    }
}