package com.radiotelescope.controller.admin.celestialBody

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.celestialBody.BaseCelestialBodyRestControllerTest
import com.radiotelescope.controller.model.celestialBody.UpdateForm
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CelestialBodyUpdateControllerTest : BaseCelestialBodyRestControllerTest() {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBodyUpdateController: CelestialBodyUpdateController
    private lateinit var celestialBody: CelestialBody

    private val baseUpdateForm = UpdateForm(
            name = "The Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    @Before
    override fun init() {
        super.init()

        celestialBodyUpdateController = CelestialBodyUpdateController(
                celestialBodyWrapper = getWrapper(),
                logger = getLogger()
        )

        val user = testUtil.createUser("cspath1@ycp.edu")

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        // Persist a coordinate
        val coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34,
                        seconds = 32
                ),
                declination = 22.0
        )
        coordinateRepo.save(coordinate)

        celestialBody = testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinate
        )
    }

    @Test
    fun testSuccessResponse() {
        val result = celestialBodyUpdateController.execute(
                id = celestialBody.id,
                form = baseUpdateForm
        )

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
    fun testFailedFormValidationResponse() {
        val formCopy = baseUpdateForm.copy(name = null)

        val result = celestialBodyUpdateController.execute(
                id = celestialBody.id,
                form = formCopy
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        val result = celestialBodyUpdateController.execute(
                id = 311L,
                form = baseUpdateForm
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Simulate a logout
        getContext().logout()
        getContext().currentRoles = mutableListOf()

        val result = celestialBodyUpdateController.execute(
                id = celestialBody.id,
                form = baseUpdateForm
        )

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