package com.radiotelescope.contracts.user

import com.radiotelescope.contracts.BaseUserWrapperTest
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
class UserUserWrapperTest : BaseUserWrapperTest() {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private val baseCreateRequest = Register.Request(
            firstName = "Cody",
            lastName = "Spath",
            email = "codyspath@gmail.com",
            phoneNumber = "717-823-2216",
            password = "ValidPassword",
            passwordConfirm = "ValidPassword",
            company = "York College of PA",
            categoryOfService = UserRole.Role.GUEST
    )

    val context = FakeUserContext()
    lateinit var factory: BaseUserFactory
    lateinit var wrapper: UserUserWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseUserFactory(userRepo, userRoleRepo)
        wrapper = UserUserWrapper(context, factory, userRepo, userRoleRepo)
    }

    @Test
    fun testValidRegistration_Success() {
        wrapper.register(
                request = baseCreateRequest
        ).execute()
        assertTrue(executed)
    }
}