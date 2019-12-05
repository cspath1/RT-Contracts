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
internal class SensorStatusRetrieveControllerTest: BaseSensorStatusRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var sensorStatusRetrieveController: SensorStatusRetrieveController
    private lateinit var user: User
    private lateinit var sensorStatus: SensorStatus

    @Before
    override fun init() {
        super.init()

        sensorStatusRetrieveController = SensorStatusRetrieveController(
                sensorStatusWrapper = getWrapper(),
                logger = getLogger()
        )

        user = testUtil.createUser("sampleuser@ycp.edu")

        sensorStatus = testUtil.createSensorStatus(
                gate = 0,
                proximity = 0,
                azimuthMotor = 0,
                elevationMotor = 0,
                weatherStation = 0
        )
    }

    @Test
    fun testSuccessResponse() {
        // Simulate a login
        getContext().login(user.id)
        getContext().currentRoles.add(UserRole.Role.ADMIN)

        val result = sensorStatusRetrieveController.execute(sensorStatus.id)

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
}