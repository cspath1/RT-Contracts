package com.radiotelescope.controller.celestialBody

import com.radiotelescope.contracts.celestialBody.CelestialBodyInfo
import com.radiotelescope.controller.admin.celestialBody.CelestialBodyRetrieveController
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
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
internal class CelestialBodyRetrieveControllerTest : BaseCelestialBodyRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBodyRetrieveController: CelestialBodyRetrieveController
    private lateinit var user: User
    private lateinit var celestialBody: CelestialBody

    @Before
    override fun init() {
        super.init()

        celestialBodyRetrieveController = CelestialBodyRetrieveController(
                celestialBodyWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

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
        val result = celestialBodyRetrieveController.execute(celestialBody.id)

        assertNotNull(result)
        assertTrue(result.data is CelestialBodyInfo)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedValidationResponse() {
        val result = celestialBodyRetrieveController.execute(311L)

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

        val result = celestialBodyRetrieveController.execute(celestialBody.id)

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