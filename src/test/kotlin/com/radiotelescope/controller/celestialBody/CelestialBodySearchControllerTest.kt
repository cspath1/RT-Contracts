package com.radiotelescope.controller.celestialBody

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
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class CelestialBodySearchControllerTest : BaseCelestialBodyRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBodySearchController: CelestialBodySearchController
    private lateinit var celestialBody: CelestialBody

    @Before
    override fun init() {
        super.init()

        celestialBodySearchController = CelestialBodySearchController(
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
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 5,
                        minutes = 34
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
        val result = celestialBodySearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                value = celestialBody.name,
                search = "name"
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
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
        val result = celestialBodySearchController.execute(
                pageNumber = 0,
                pageSize = 10,
                value = celestialBody.name,
                search = ""
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

        val result = celestialBodySearchController.execute(
                pageSize = 10,
                pageNumber = 0,
                value = celestialBody.name,
                search = "name"
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

    @Test
    fun testInvalidPageParametersResponse() {
        // Test the scenario where the page parameters supplied
        // are invalid
        val result = celestialBodySearchController.execute(
                pageNumber = -1,
                pageSize = -1,
                value = "eowignweogni",
                search = "name"
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
}