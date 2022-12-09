package com.radiotelescope.controller.sensorOverrides

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
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
internal class SensorOverridesRetrieveListControllerTest: BaseSensorOverridesRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var sensorOverridesRetrieveListController: SensorOverridesRetrieveListController

    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        sensorOverridesRetrieveListController = SensorOverridesRetrieveListController(
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

        val result = sensorOverridesRetrieveListController.execute()

        assertNotNull(result)
        assertTrue(result.data is List<*>)
        assertEquals(SensorOverrides.Name.values().size, (result.data as List<*>).size)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // do not log the user in
        val result = sensorOverridesRetrieveListController.execute()

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