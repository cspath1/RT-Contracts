package com.radiotelescope.controller.updateEmailToken

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
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
internal class UserUpdateEmailControllerTest : BaseUpdateEmailTokenRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var userUpdateEmailController: UserUpdateEmailController
    private lateinit var updateEmailToken: UpdateEmailToken
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        userUpdateEmailController = UserUpdateEmailController(
                updateEmailTokenWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        updateEmailToken = testUtil.createUpdateEmailToken(
                user = user,
                token = "AGoodToken",
                email = "spathcody@gmail.com"
        )
    }

    @Test
    fun testSuccessResponse() {
        val result = userUpdateEmailController.execute(updateEmailToken.token)

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidTokenResponse() {
        val result = userUpdateEmailController.execute("ABadToken")

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }
}