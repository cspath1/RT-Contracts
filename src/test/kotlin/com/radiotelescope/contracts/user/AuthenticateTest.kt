package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
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
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class AuthenticateTest {
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

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val baseRequest = Authenticate.Request(
            email = "cspath1@ycp.edu",
            password = "Password"
    )

    @Before
    fun setUp() {
        // We will need to hash the password before persisting
        val passwordEncoder = Pbkdf2PasswordEncoder(
                "YCAS2018",
                50,
                256
        )
        val encodedPassword = passwordEncoder.encode("Password")

        // Persist the User with the hashed password
        val user = testUtil.createUserWithEncodedPassword(
                email = "cspath1@ycp.edu",
                password = encodedPassword,
                accountHash = "Test Account 1"
        )

        // Make sure this was correctly executed
        assertEquals(1, userRepo.count())
        assertNotNull(user)
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command
        val (info, errors) = Authenticate(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The info class should not be null
        assertNotNull(info)

        // The errors should be
        assertNull(errors)
    }

    @Test
    fun testBlankEmail_Failure() {
        // Create a copy of the request with a blank email
        val requestCopy = baseRequest.copy(
                email = ""
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testBlankPassword_Failure() {
        // Create a copy of the request with a blank password
        val requestCopy = baseRequest.copy(
                password = ""
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testNonExistentEmail_Failure() {
        // Create a copy of the request with a non-existent email
        val requestCopy = baseRequest.copy(
                email = "spathcody@gmail.com"
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }

    @Test
    fun testPasswordsDoNotMatch_Failure() {
        // Create a copy of the request with a bad password
        val requestCopy = baseRequest.copy(
                password = "Passwrod"
        )

        // Execute the command
        val (info, errors) = Authenticate(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // The errors should not be null
        assertNotNull(errors)

        // The data class should be
        assertNull(info)
    }
}