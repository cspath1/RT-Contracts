package com.radiotelescope.contracts.accountActivateToken

import com.radiotelescope.AbstractSpringTest
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
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ActivateAccountTest : AbstractSpringTest() {
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
        user.status = User.Status.INACTIVE
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
        assertEquals(User.Status.ACTIVE, user.status)

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