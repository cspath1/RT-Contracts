package com.radiotelescope.contracts.user

import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateTest {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val registerRequest = Register.Request(
            firstName = "Rathana",
            lastName = "Pim",
            email = "rpim@ycp.edu",
            phoneNumber = "540-532-8081",
            password = "ValidPassword1",
            passwordConfirm = "ValidPassword1",
            company = "York College of Pennsylvania",
            categoryOfService = UserRole.Role.GUEST
    )


    @Test
    fun testValidConstraints_OptionalFields_Success() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)

        // Should still now have 1 user and 1 user role
        assertEquals(1, userRepo.count())
        assertEquals(1, userRoleRepo.count())

        // Grab the user
        val user = userRepo.findById(id!!).get()

        // Ensure all fields were properly set
        assertEquals("Cody", user.firstName)
        assertEquals("Spath", user.lastName)
        assertEquals("cspath1@ycp.edu", user.email)
        assertEquals("York College of Pennsylvania", user.company)
        assertEquals("717-823-2216", user.phoneNumber)

        val roles = userRoleRepo.findAllByUserId(id)

        // Ensure the role was properly set
        assertEquals(1, roles.size)
        assertEquals(UserRole.Role.GUEST, roles[0].role)
    }

    @Test
    fun testValidConstraints_NoOptionalFields_Success() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = null,
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = null,
                categoryOfService = UserRole.Role.GUEST
        )


        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)

        // Should now have 1 user and 1 user role
        assertEquals(1, userRepo.count())
        assertEquals(1, userRoleRepo.count())

        // Grab the user
        val user = userRepo.findById(id!!).get()

        // Ensure all fields were properly set
        assertEquals("Cody", user.firstName)
        assertEquals("Spath", user.lastName)
        assertEquals("cspath1@ycp.edu", user.email)
        assertNull(user.company)
        assertNull(user.phoneNumber)

        val roles = userRoleRepo.findAllByUserId(id)

        // Ensure the role was properly set
        assertEquals(1, roles.size)
        assertEquals(UserRole.Role.GUEST, roles[0].role)
    }

    @Test
    fun testBlankFirstName_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the first name
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testFirstNameLong_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody".repeat(50),
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the first name
        assertTrue(error!![ErrorTag.FIRST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankLastName_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the last name
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    ///////////////////////////////////////////////////////////////////////
    @Test
    fun testLastNameLong_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath".repeat(50),
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the last name
        assertTrue(error!![ErrorTag.LAST_NAME].isNotEmpty())
    }

    @Test
    fun testBlankEmail_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath",
                email = "",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testInvalidEmail_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "spath",
                email = "not an email",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testEmailInUse_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )

        val newUserRequest = registerRequest.copy(
                email = baseRequest.email
        )

        // Persist a User Entity
        val user = userRepo.save(newUserRequest.toEntity())
        assertNotNull(user)

        // Now, executing the command should result in an error
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the email
        assertTrue(error!![ErrorTag.EMAIL].isNotEmpty())
    }

    @Test
    fun testBlankPassword_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "",
                passwordConfirm = "",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testPasswordsDoNotMatch_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "NOMATCH",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password confirm not matching
        assertTrue(error!![ErrorTag.PASSWORD_CONFIRM].isNotEmpty())
    }

    @Test
    fun testPasswordRegexNotAMatch_Failure() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "Password",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNull(id)
        assertNotNull(error)

        // Ensure it failed because of the password
        assertTrue(error!![ErrorTag.PASSWORD].isNotEmpty())
    }

    @Test
    fun testNewRole_Success(){
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.STUDENT
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)

        val roles = userRoleRepo.findAllByUserId(id!!)

        // Ensure new and old roles are both set
        assertEquals(2, roles.size)
        assertEquals(UserRole.Role.GUEST, roles[0].role)
        assertEquals(UserRole.Role.STUDENT, roles[1].role)

    }

    // The same as testValidConstraints_OptionalFields_Success()
    @Test
    fun testSameRole_Success() {
        // Execute Register Command
        Register(
                request = registerRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Execute Update Command
        val baseRequest = Update.Request(
                id = userRepo.findByEmail(registerRequest.email)!!.id,
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                phoneNumber = "717-823-2216",
                password = "ValidPassword1",
                passwordConfirm = "ValidPassword1",
                company = "York College of Pennsylvania",
                categoryOfService = UserRole.Role.GUEST
        )
        // Execute the command
        val (id, error) = Update(
                request = baseRequest,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(error)
        assertNotNull(id)

        // Should still now have 1 user and 1 user role
        assertEquals(1, userRepo.count())
        assertEquals(1, userRoleRepo.count())

        // Grab the user
        val user = userRepo.findById(id!!).get()

        // Ensure all fields were properly set
        assertEquals("Cody", user.firstName)
        assertEquals("Spath", user.lastName)
        assertEquals("cspath1@ycp.edu", user.email)
        assertEquals("York College of Pennsylvania", user.company)
        assertEquals("717-823-2216", user.phoneNumber)

        val roles = userRoleRepo.findAllByUserId(id)

        // Ensure the role was properly set
        assertEquals(1, roles.size)
        assertEquals(UserRole.Role.GUEST, roles[0].role)
    }
}

