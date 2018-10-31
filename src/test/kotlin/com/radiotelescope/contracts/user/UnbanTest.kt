package com.radiotelescope.contracts.user

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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UnbanTest {
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
    private lateinit var userRepo: IUserRepository

    private var userId = -1L
    private var userId2 = -2L

    @Before
    fun setUp() {
        // Persist a user
        val theUser = testUtil.createUser("cspath1@ycp.edu")
        val theUser2 = testUtil.createUser("testemail@ycp.edu")

        userId = theUser.id
        testUtil.setInactiveStatus(theUser)

        userId2 = theUser2.id
    }

    @Test
    fun inactiveUserTest(){
        val theUser = userRepo.findById(userId)

        assertTrue(theUser.get().status == User.Status.Inactive)
        assertTrue(!theUser.get().active)
    }

    @Test
    fun unbanUserTest(){
        val theUser = userRepo.findById(userId)
        val (id, errors) = Unban(
                id = userId,
                userRepo = userRepo
        ).execute()

        assertTrue(errors == null)
        assertTrue(id == userId)

        assertTrue(theUser.get().status == User.Status.Active)
        assertTrue(theUser.get().active)

    }

    @Test
    fun unbanNonexistingUserTest(){
        val (id, errors) = Unban(
                id = 10,
                userRepo = userRepo
        ).execute()

        assertTrue(id == null)
        assertFalse(errors == null)
    }

    @Test
    fun unbanNotBannedUserTest(){
        val (id, errors) = Unban(
                id = userId2,
                userRepo = userRepo
        ).execute()

        assertTrue(errors != null)
        assertTrue(id == null)
    }

    @Test
    fun unbanActiveUserTest(){
        val (id, errors) = Unban(
                id = userId2,
                userRepo = userRepo
        ).execute()

        assertTrue(errors != null)
        assertTrue(id == null)
    }

}