package com.radiotelescope.controller.appointment.request

import com.radiotelescope.TestUtil
import com.radiotelescope.controller.appointment.BaseAppointmentRestControllerTest
import com.radiotelescope.controller.model.appointment.request.CelestialBodyAppointmentRequestForm
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.services.ses.MockAwsSesSendService
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
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class CelestialBodyAppointmentRequestControllerTest : BaseAppointmentRestControllerTest() {
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBodyAppointmentRequestController: CelestialBodyAppointmentRequestController
    private lateinit var user: User
    private lateinit var celestialBody: CelestialBody

    private val baseForm = CelestialBodyAppointmentRequestForm(
            userId = -1L,
            startTime = Date(System.currentTimeMillis() + 50000L),
            endTime = Date(System.currentTimeMillis() + 100000L),
            telescopeId = 1L,
            isPublic = true,
            celestialBodyId = -1L
    )

    @Before
    override fun init() {
        super.init()

        celestialBodyAppointmentRequestController = CelestialBodyAppointmentRequestController(
                appointmentWrapper = getCelestialBodyCreateWrapper(),
                awsSesSendService = MockAwsSesSendService(true),
                logger = getLogger(),
                userRepo = userRepo
        )

        user = testUtil.createUser("cspath1@ycp.edu")

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )

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
        // Make a copy of the base form with a valid user and celestial body
        val formCopy = baseForm.copy(
                userId = user.id,
                celestialBodyId = celestialBody.id
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = celestialBodyAppointmentRequestController.execute(formCopy)

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
    fun testInvalidFormResponse() {
        // Make a copy of the form with a valid id and null startTime
        val formCopy = baseForm.copy(
                userId = user.id,
                startTime = null
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = celestialBodyAppointmentRequestController.execute(formCopy)

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
        // Make a copy of the form with a valid user id and invalid celestial body id
        val formCopy = baseForm.copy(
                userId = user.id,
                celestialBodyId = 311L
        )

        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.addAll(listOf(UserRole.Role.MEMBER, UserRole.Role.USER))

        val result = celestialBodyAppointmentRequestController.execute(formCopy)

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
        // Do not log the user in
        val formCopy = baseForm.copy(
                userId = user.id
        )

        val result = celestialBodyAppointmentRequestController.execute(formCopy)

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