package com.radiotelescope.controller.user

import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UserActivateAccountControllerTest : BaseActivateAccountRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userActivateAccountController: UserActivateAccountController

    private lateinit var user: User

    private lateinit var token: AccountActivateToken

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("rpim@ycp.edu")

        userActivateAccountController = UserActivateAccountController(
                activateTokenWrapper = getWrapper(),
                logger = getLogger()
        )

        token = testUtil.createAccountActivateToken(
                user = user,
                token = "someToken"
        )
    }

    @Test
    fun testSuccessResponse() {
        // Test the success scenario to ensure the result
        // is correct
        val result = userActivateAccountController.execute(token.token)

        assertNotNull(result)
        assertEquals(user.id, result.data)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        // Test the scenario where the form is valid,
        // but validation in the command object fails
        val result = userActivateAccountController.execute("notToken")

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }
}