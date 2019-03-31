package com.radiotelescope.contracts.updateEmailToken

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
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
internal class BaseUpdateEmailTokenFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    private lateinit var factory: UpdateEmailTokenFactory

    @Before
    fun init() {
        factory = BaseUpdateEmailTokenFactory(
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        )
    }

    @Test
    fun requestUpdateEmail(){
        // Call the factory method
        val cmd = factory.requestUpdateEmail(
                request = CreateUpdateEmailToken.Request(
                        email = "rpim@ycp.edu",
                        emailConfirm = "rpim@ycp.edu",
                        userId = 1L
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is CreateUpdateEmailToken)
    }

    @Test
    fun updateEmail(){
        // Call the factory method
        val cmd = factory.updateEmail(
                token = "someToken"
        )

        // Ensure it is the correct command
        assertTrue(cmd is UpdateEmail)
    }
}