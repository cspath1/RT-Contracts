package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorStatus.SensorStatus
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SensorStatusGetMostRecentControllerTest: BaseSensorStatusRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var sensorStatusGetMostRecentController: SensorStatusGetMostRecentController
    private lateinit var user: User

    @Before
    override fun init() {
        super.init()

        sensorStatusGetMostRecentController = SensorStatusGetMostRecentController(
                sensorStatusWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("sampleuser@ycp.edu")

        testUtil.createSensorStatus(0, 0, 0, 0, 0)
        testUtil.createSensorStatus(0, 1, 1, 0, 0)
        testUtil.createSensorStatus(0, 1, 2, 1, 1)
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = sensorStatusGetMostRecentController.execute()

        Assert.assertNotNull(result)
        Assert.assertTrue(result.data is SensorStatus)
        Assert.assertEquals(HttpStatus.OK, result.status)
        Assert.assertNull(result.errors)

        // Ensure a log record was created
        Assert.assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            Assert.assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testFailedAuthenticationResponse() {
        // Do not log the user in
        val result = sensorStatusGetMostRecentController.execute()

        Assert.assertNotNull(result)
        Assert.assertNull(result.data)
        Assert.assertEquals(HttpStatus.FORBIDDEN, result.status)
        Assert.assertNotNull(result.errors)

        // Ensure a log record was created
        Assert.assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            Assert.assertEquals(HttpStatus.FORBIDDEN.value(), it.status)
        }
    }
}