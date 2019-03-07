package com.radiotelescope.controller.admin.celestialBody

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.celestialBody.BaseCelestialBodyRestControllerTest
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
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CelestialBodyListControllerTest : BaseCelestialBodyRestControllerTest() {
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

    private lateinit var celestialBodyListController: CelestialBodyListController
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        celestialBodyListController = CelestialBodyListController(
                celestialBodyWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("cspath1@ycp.edu")

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

        // Persist a celestial body with a coordinate
        testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinate
        )

        // Persist another one without a coordinate
        testUtil.createCelestialBody(
                name = "The Sun",
                coordinate = null
        )
    }

    @Test
    fun testSuccessResponse() {
        val result = celestialBodyListController.execute(
                pageNumber = 0,
                pageSize = 20
        )

        assertNotNull(result)
        assertTrue(result.data is Page<*>)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)
    }

    @Test
    fun testInvalidPageParamsResponse() {
        val result = celestialBodyListController.execute(
                pageNumber = -1,
                pageSize = -1
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

        val result = celestialBodyListController.execute(
                pageSize = 10,
                pageNumber = 0
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