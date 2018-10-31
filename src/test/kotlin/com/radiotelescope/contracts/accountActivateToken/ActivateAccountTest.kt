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
import java.util.*
import liquibase.integration.spring.SpringLiquibase



@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class ActivateAccountTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil()
        }

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
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private lateinit var token: AccountActivateToken
    private lateinit var user: User

    @Before
    fun init() {
        // Persist the user and token
        user = testUtil.createUser("cspath1@ycp.edu")

        // Set the user to inactive
        user.status = User.Status.Inactive
        user.active = false
        userRepo.save(user)

        token = testUtil.createAccountActivateToken(
                user = user,
                token = "goodToken"
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (id, error) = ActivateAccount(
                token = token.token,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        ).execute()

        // Ensure it was a success
        assertNotNull(id)
        assertNull(error)

        // Ensure the user is now active
        assertTrue(user.active)
        assertEquals(User.Status.Active, user.status)

        // Ensure the record no longer exists
        assertFalse(accountActivateTokenRepo.existsByToken(token.token))
    }

    @Test
    fun testExpirationDateInPast_Failure() {
        token.expirationDate = Date(System.currentTimeMillis() - 1000000L)
        accountActivateTokenRepo.save(token)

        val (id, error) = ActivateAccount(
                token = token.token,
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        ).execute()

        // Ensure it was a failure
        assertNull(id)
        assertNotNull(error)

        assertTrue(error!![ErrorTag.EXPIRATION_DATE].isNotEmpty())
    }

    @Test
    fun testInvalidToken_Failure() {
        val (id, error) = ActivateAccount(
                token = "Bedeviled is a good movie",
                accountActivateTokenRepo = accountActivateTokenRepo,
                userRepo = userRepo
        ).execute()

        // Ensure it was a failure
        assertNull(id)
        assertNotNull(error)

        assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }
}