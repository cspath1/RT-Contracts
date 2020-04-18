package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseUserFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    private lateinit var factory: UserFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseUserFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                accountActivateTokenRepo = accountActivateTokenRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        )
    }

    @Test
    fun register() {
        // Call the factory method
        val cmd = factory.register(
                request = Register.Request(
                        firstName = "Cody",
                        lastName = "Spath",
                        email = "cspath1@ycp.edu",
                        emailConfirm = "cspath1@ycp.edu",
                        phoneNumber = "717-823-2216",
                        password = "ValidPassword",
                        passwordConfirm = "ValidPassword",
                        company = "York College of Pennsylvania",
                        categoryOfService = UserRole.Role.GUEST
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Register)
    }

    @Test
    fun authenticate() {
        // Call the factory method
        val cmd = factory.authenticate(
                request = Authenticate.Request(
                        email = "cspath1@ycp.edu",
                        password = "Password"
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Authenticate)
    }

    @Test
    fun retrieve() {
        // Call the factory method
        val cmd = factory.retrieve(
                id = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Retrieve)
    }

    @Test
    fun list() {
        // Call the factory method
        val cmd = factory.list(
                pageable = PageRequest.of(0, 10)
        )

        // Ensure it is the correct command
        assertTrue(cmd is List)
    }

    @Test
    fun update() {
        // Call the factory method
        val cmd = factory.update(
                request = Update.Request(
                        id = 123456789,
                        firstName = "Cody",
                        lastName = "Spath",
                        phoneNumber = "717-823-2216",
                        company = "York College of Pennsylvania",
                        profilePicture = "codypic.jpg",
                        profilePictureApproved = false,
                        notificationType = "SMS"
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Update)
    }

    @Test
    fun delete() {
        // Call the factory method
        val cmd = factory.delete(
                id = 311L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Delete)
    }

    @Test
    fun ban() {
        // Call the factory method
        val cmd = factory.ban(
                id = 311L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Ban)
    }

    @Test
    fun unban() {
        // Call the factory method
        val cmd = factory.unban(
                id = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Unban)
    }

    @Test
    fun changePassword() {
        // Call the factory method
        val cmd = factory.changePassword(
                request = ChangePassword.Request(
                        currentPassword = "Password",
                        passwordConfirm = "Password1@",
                        password = "Password1@",
                        id = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is ChangePassword)
    }

    @Test
    fun search() {
        // Call the factory method
        val cmd = factory.search(
                searchCriteria = listOf(),
                pageable = PageRequest.of(0, 10)
        )

        // Ensure it is the correct command
        assertTrue(cmd is Search)
    }

    @Test
    fun invite(){
        // Call the factory method
        val cmd = factory.invite(
                email = "email@email.email"
        )

        // Ensure it is the correct command
        assertTrue(cmd is Invite)
    }
}