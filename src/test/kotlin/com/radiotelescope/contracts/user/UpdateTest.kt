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
internal class UpdateTest {
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


    private lateinit var user: User
    private lateinit var otherUser: User

    private var userId = -1L
    private var otherUserId = -1L

    @Before
    fun init(){
        // Persist the users
        user = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account 1"
        )
        otherUser = testUtil.createUser(
                email = "rpim1@ycp.edu",
                accountHash = "Test Account 2"
        )

        // Set the user Id
        userId = user.id
        otherUserId = otherUser.id
    }


    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (id, error) = Update(
            request = Update.Request(
                    id = userId,
                    firstName = "Evil",
                    lastName = "Twin",
                    phoneNumber = "717-000-0000",
                    company = "Evil Twin Company"
            ),
            userRepo = userRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)
    }



    @Test
    fun testBlankFirstName_Failure() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        id = userId,
                        firstName = "",
                        lastName = "Twin",
                        phoneNumber = "717-000-0000",
                        company = "Evil Twin Company"
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testFirstNameLong_Failure() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        id = userId,
                        firstName = "Evil".repeat(50),
                        lastName = "Twin",
                        phoneNumber = "717-000-0000",
                        company = "Evil Twin Company"
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankLastName_Failure() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        id = userId,
                        firstName = "Evil",
                        lastName = "",
                        phoneNumber = "717-000-0000",
                        company = "Evil Twin Company"
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testLastNameLong_Failure() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        id = userId,
                        firstName = "Evil",
                        lastName = "Twin".repeat(50),
                        phoneNumber = "717-000-0000",
                        company = "Evil Twin Company"
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testUserDoesNotExist_Failure() {
        // Execute the command
        val (id, error) = Update(
                request = Update.Request(
                        id = 123456789,
                        firstName = "Evil",
                        lastName = "Twin",
                        phoneNumber = "717-000-0000",
                        company = "Evil Twin Company"
                ),
                userRepo = userRepo
        ).execute()

        // Should have failed
        assertNotNull(error)
        assertNull(id)
        assertTrue(error!![ErrorTag.ID].isNotEmpty())
    }
}

