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
internal class DeleteTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

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
        val (id, errors) = Delete(
                id = userId,
                userRepo = userRepo
        ).execute()

        // The success data type should not
        // be null
        assertNotNull(id)

        // The errors should be though
        assertNull(errors)

        val theUser = userRepo.findById(id!!)

        assertTrue(theUser.isPresent)

        assertEquals(User.Status.DELETED, theUser.get().status)
        assertFalse(theUser.get().active)
    }

    @Test
    fun testInvalidUserId_Failure() {
        // Execute the command
        val (id, errors) = Delete(
                id = 311L,
                userRepo = userRepo
        ).execute()

        // The success data type should be null
        assertNull(id)

        // The errors should not be
        assertNotNull(errors)

        assertTrue(errors!![ErrorTag.ID].isNotEmpty())
    }
}