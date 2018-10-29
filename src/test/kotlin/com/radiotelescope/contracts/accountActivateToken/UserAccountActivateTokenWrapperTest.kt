package com.radiotelescope.contracts.accountActivateToken

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
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
internal class UserAccountActivateTokenWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private lateinit var wrapper: UserAccountActivateTokenWrapper
    private lateinit var factory: AccountActivateTokenFactory

    private lateinit var user: User
    private lateinit var token: AccountActivateToken

    @Before
    fun init() {
        // Initialize the factory
        factory = BaseAccountActivateTokenFactory(
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        )

        // Initialize the wrapper
        wrapper = UserAccountActivateTokenWrapper(factory)

        // Persist a user and token
        user = testUtil.createUser("cspath1@ycp.edu")

        // Set the user to inactive
        user.active = false
        user.status = User.Status.Inactive
        userRepo.save(user)

        token = testUtil.createAccountActivateToken(
                user = user,
                token = "HELP"
        )
    }

    @Test
    fun testValidConstraints_ActivateAccount_Success() {
        val (id, error) = wrapper.activateAccount(
                token = token.token
        ).execute()

        assertNotNull(id)
        assertNull(error)
    }
}