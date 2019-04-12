package com.radiotelescope.controller.celestialBody

import com.radiotelescope.controller.admin.celestialBody.CelestialBodyCreateController
import com.radiotelescope.controller.model.celestialBody.CreateForm
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
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
internal class CelestialBodyCreateControllerTest : BaseCelestialBodyRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var celestialBodyCreateController: CelestialBodyCreateController

    private lateinit var user: User

    private val userContext = getContext()

    private val baseForm = CreateForm(
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 20.0
    )

    @Before
    override fun init() {
        super.init()

        user = testUtil.createUser("cspath1@ycp.edu")

        // Simulate a login
        userContext.login(user.id)
        userContext.currentRoles.add(UserRole.Role.ADMIN)

        celestialBodyCreateController = CelestialBodyCreateController(
                celestialBodyWrapper = getWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        val result = celestialBodyCreateController.execute(baseForm)

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidFormResponse() {
        // Make a copy of the form with a null name
        val formCopy = baseForm.copy(
                name = null
        )

        val result = celestialBodyCreateController.execute(formCopy)

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

    @Test
    fun testValidForm_FailedValidationResponse() {
        // Make a copy of the form with a null declination
        // when the celestial body needs it
        val formCopy = baseForm.copy(
                declination = null
        )

        val result = celestialBodyCreateController.execute(formCopy)

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

    @Test
    fun testFailedAuthenticationResponse() {
        // Simulate a logout
        userContext.logout()
        userContext.currentRoles = mutableListOf()

        val result = celestialBodyCreateController.execute(baseForm)

        assertNotNull(result)
        assertNull(result.data)
        assertNotNull(result.errors)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertEquals(1, result.errors!!.size)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}