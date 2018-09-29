package com.radiotelescope.contracts.user

import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseUserFactoryTest {

    @Autowired
    private lateinit var userRepo:IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    lateinit var factory: UserFactory

    @Before
    fun init() {
        factory = BaseUserFactory(userRepo, userRoleRepo)
    }

    @Test
    fun register() {
        // Call the factory method
        val cmd = factory.register(
                request = Register.Request(
                        firstName = "Cody",
                        lastName = "Spath",
                        email = "cspath1@ycp.edu",
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
}