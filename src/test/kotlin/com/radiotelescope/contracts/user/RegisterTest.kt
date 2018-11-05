package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
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
internal class RegisterTest {
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private val baseRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "cspath1@ycp.edu",
            emailConfirm = "cspath1@ycp.edu",
            phoneNumber = "717-823-2216",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.STUDENT
    )

    @Test
    fun testValidConstraints_OptionalFields_Success() {
        // Execute the command
        val (success, error) = Register(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(success)

        // Should now have 1 user and 2 user roles
        assertEquals(1, userRepo.count())
        assertEquals(2, userRoleRepo.count())

        // Grab the user
        val user = userRepo.findById(success!!.id).get()

        // Ensure all fields were properly set
        assertEquals("Cody", user.firstName)
        assertEquals("Spath", user.lastName)
        assertEquals("cspath1@ycp.edu", user.email)
        assertEquals("York College of Pennsylvania", user.company)
        assertEquals("717-823-2216", user.phoneNumber)

        val roles = userRoleRepo.findAllByUserId(user.id)

        // Ensure the role was properly set
        assertEquals(2, roles.size)
        roles.forEach {
            if (it.role != UserRole.Role.STUDENT && it.role != UserRole.Role.USER) {
                fail("An unknown enum value was persisted")
            }
            if (it.role == UserRole.Role.STUDENT)
                assertFalse(it.approved)
        }
    }

    @Test
    fun testValidConstraints_NoOptionalFields_Success() {
        // Set the optional fields to null
        val requestCopy = baseRequest.copy(
                phoneNumber = null,
                company = null
        )

        // Execute the command
        val (success, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(success)

        // Should now have 1 user and 2 user role
        assertEquals(1, userRepo.count())
        assertEquals(2, userRoleRepo.count())

        // Grab the user
        val user = userRepo.findById(success!!.id).get()

        // Ensure all fields were properly set
        assertEquals("Cody", user.firstName)
        assertEquals("Spath", user.lastName)
        assertEquals("cspath1@ycp.edu", user.email)
        assertNull(user.company)
        assertNull(user.phoneNumber)

        val roles = userRoleRepo.findAllByUserId(user.id)

        // Ensure the role was properly set
        assertEquals(2, roles.size)
        roles.forEach {
            if (it.role != UserRole.Role.STUDENT && it.role != UserRole.Role.USER) {
                fail("An unknown enum value was persisted")
            }
            if (it.role == UserRole.Role.STUDENT)
                assertFalse(it.approved)
        }
    }

    @Test
    fun testValidConstraints_GuestApproved_Success() {
        // Set the category of service to guest, meaning
        // it will be approved
        val requestCopy = baseRequest.copy(
                categoryOfService = UserRole.Role.GUEST
        )

        // Execute the command
        val (success, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(success)

        // Should now have 1 user and 2 user roles
        assertEquals(1, userRepo.count())
        assertEquals(2, userRoleRepo.count())

        // Since we have already confirmed that the values
        // are set accordingly in the above test case, let's
        // just make sure the user roles are both approved
        val theUser = userRepo.findById(success!!.id).get()

        val roles = userRoleRepo.findAllByUserId(theUser.id)

        roles.forEach {
            assertTrue(it.approved)
        }
    }

    @Test
    fun testBlankFirstName_Failure() {
        // Set the first name field to blank
        val requestCopy = baseRequest.copy(
                firstName = ""
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the first name
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testFirstNameLong_Failure() {
        // Set the first name field to blank
        val requestCopy = baseRequest.copy(
                firstName = "Cody".repeat(50)
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the first name
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankLastName_Failure() {
        // Set the last name to blank
        val requestCopy = baseRequest.copy(
                lastName = ""
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the last name
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testLastNameLong_Failure() {
        // Set the last name to be longer than the column size
        val requestCopy = baseRequest.copy(
                lastName = "Spath".repeat(40)
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the last name
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankEmail_Failure() {
        // Set the email to be blank
        val requestCopy = baseRequest.copy(
                email = ""
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalidEmail_Failure() {
        // Set the email to not be an email address
        val requestCopy = baseRequest.copy(
                email = "not an email"
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testEmailInUse_Failure() {
        // Persist a User Entity
        val user = userRepo.save(baseRequest.toEntity())
        assertNotNull(user)

        // Now, executing the command should result in an error
        // Execute the command
        val (token, error) = Register(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testEmailsDoNotMatch_Failure() {
        // Set the emails to be different values
        val requestCopy = baseRequest.copy(
                emailConfirm = "spathcody@gmail.com"
        )

        // Now, execute the command
        val (id, errors) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(errors)

        // Ensure it failed because of the emails not matching
        assertTrue(errors!![ErrorTag.EMAIL_CONFIRM].isNotEmpty())
    }

    @Test
    fun testBlankPassword_Failure() {
        // Set the password to blank
        val requestCopy = baseRequest.copy(
                password = ""
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testPasswordsDoNotMatch_Failure() {
        // Set the password confirm to something different than the password
        val requestCopy = baseRequest.copy(
                passwordConfirm = "BeepBoopBop"
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the password confirm not matching
        assertTrue(error!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testPasswordRegexNotAMatch_Failure() {
        // Set the password to something that will not pass validation
        val requestCopy = baseRequest.copy(
                password = "Password"
        )

        // Execute the command
        val (token, error) = Register(
                request = requestCopy,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        ).execute()

        // Should have failed
        assertNull(token)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }
}