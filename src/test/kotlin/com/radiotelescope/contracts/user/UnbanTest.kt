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
internal class UnbanTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    private var userId = -1L
    private var userId2 = -2L

    @Before
    fun setUp() {
        // Persist a user
        val theUser = testUtil.createUser("cspath1@ycp.edu")
        val theUser2 = testUtil.createUser("testemail@ycp.edu")

        userId = theUser.id
        testUtil.banUser(theUser)

        userId2 = theUser2.id
    }

    @Test
    fun inactiveUserTest(){
        val theUser = userRepo.findById(userId)

        assertEquals(User.Status.BANNED, theUser.get().status)
        assertFalse(theUser.get().active)
    }

    @Test
    fun unbanUserTest(){
        val theUser = userRepo.findById(userId)
        val (theResponse, errors) = Unban(
                id = userId,
                userRepo = userRepo
        ).execute()

        assertNull(errors)
        assertEquals(userId, theResponse!!.id)

        assertEquals(User.Status.ACTIVE, theUser.get().status)
        assertTrue(theUser.get().active)

    }

    @Test
    fun unbanNonExistingUserTest(){
        val (theResponse, errors) = Unban(
                id = 10,
                userRepo = userRepo
        ).execute()

        assertNull(theResponse)
        assertNotNull(errors)
    }

    @Test
    fun unbanNotBannedUserTest(){
        val (theResponse, errors) = Unban(
                id = userId2,
                userRepo = userRepo
        ).execute()

        assertNotNull(errors)
        assertNull(theResponse)
    }

    @Test
    fun unbanActiveUserTest(){
        val (theResponse, errors) = Unban(
                id = userId2,
                userRepo = userRepo
        ).execute()

        assertNotNull(errors)
        assertNull(theResponse)
    }

}