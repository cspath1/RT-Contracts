package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
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
internal class InviteTest {
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
    lateinit var testUtil: TestUtil

    @Autowired
    lateinit var userRepo: IUserRepository

    @Before
    fun setUp() {
        // Persist a User with the email
        val user = testUtil.createUser(
                email = "lferree@ycp.edu"
        )

        // Make sure this was correctly executed
        assertEquals(1, userRepo.count())
        assertNotNull(user)
    }

    @Test
    fun testValidConstraints_Success(){
        // Execute the command
        val (value, errors) = Invite(
                email = "test_whatever@email.mail",
                userRepo = userRepo
        ).execute()

        // The success value should be true
        assertTrue(value!!)

        // The errors should be
        assertNull(errors)
    }

    @Test
    fun testBlankEmail_Failure(){
        // Execute the command
        val (value, errors) = Invite(
                email = "",
                userRepo = userRepo
        ).execute()

        // The success value should be null
        assertNull(value)

        // The errors shouldn't be null
        assertNotNull(errors)
    }

    @Test
    fun testInvalidEmail_Failure(){
        // Execute the Command
        val (value, errors) = Invite(
                email = "not an email",
                userRepo = userRepo
        ).execute()

        // Success value should be null
        assertNull(value)

        // Errors shouldn't be null
        assertNotNull(errors)
    }

    @Test
    fun testEmailExistsInRepository_Failure(){
        // Execute the Command
        val (value, errors) = Invite(
                email = "lferree@ycp.edu",
                userRepo = userRepo
        ).execute()

        // The success value should be null
        assertNull(value)

        // The errors shouldn't be null
        assertNotNull(errors)
    }
}