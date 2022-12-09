package com.radiotelescope.controller.sensorOverrides

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides
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
internal class SensorOverridesUpdateControllerTest: BaseSensorOverridesRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    private lateinit var sensorOverridesUpdateController: SensorOverridesUpdateController

    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        sensorOverridesUpdateController = SensorOverridesUpdateController(
                sensorOverridesWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("jhorne@ycp.edu")

        testUtil.populateDefaultSensorOverrides()
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = sensorOverridesUpdateController.execute(
                sensorName = "GATE",
                overridden = true
        )

        assertNotNull(result)
        assertTrue(result.data is SensorOverrides)
        assertEquals(true, (result.data as SensorOverrides).overridden)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidNameResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = sensorOverridesUpdateController.execute(
                sensorName = "NOT_A_VALID_SENSOR",
                overridden = true
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.BAD_REQUEST, result.status)
        assertNotNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.BAD_REQUEST.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // do not log the user in
        val result = sensorOverridesUpdateController.execute(
                sensorName = "GATE",
                overridden = true
        )

        assertNotNull(result)
        assertNull(result.data)
        assertEquals(HttpStatus.FORBIDDEN, result.status)
        assertNotNull(result.errors)

        // Ensure a log record was created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}