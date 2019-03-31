package com.radiotelescope.contracts.accountActivateToken

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseAccountActivateTokenFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    private lateinit var factory: AccountActivateTokenFactory

    @Before
    fun init() {
        factory = BaseAccountActivateTokenFactory(
                userRepo = userRepo,
                accountActivateTokenRepo = accountActivateTokenRepo
        )
    }

    @Test
    fun activateAccount() {
        // Call the factory method
        val cmd = factory.activateAccount(
                token = "Dwayne 'The Rock' Johnson"
        )

        // Ensure it is the correct command
        assertTrue(cmd is ActivateAccount)
    }
}