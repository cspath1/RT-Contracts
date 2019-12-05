package com.radiotelescope.controller.sensorStatus

import com.radiotelescope.controller.model.Profile
import com.radiotelescope.controller.model.sensorStatus.CreateForm
import com.radiotelescope.repository.log.ILogRepository
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
internal class SensorStatusCreateControllerTest: BaseSensorStatusRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var sensorStatusCreateController: SensorStatusCreateController

    private val baseForm = CreateForm(
            gate = 0,
            proximity = 0,
            azimuthMotor = 0,
            elevationMotor = 0,
            weatherStation = 0,
            token = "testid"
    )

    @Before
    override fun init() {
        super.init()

        sensorStatusCreateController = SensorStatusCreateController(
                sensorStatusWrapper = getWrapper(),
                logger = getLogger(),
                profile = Profile.LOCAL
        )

        sensorStatusCreateController.uuid = "testid"
        //sensorStatusCreateController.profile = "LOCAL"
    }

    @Test
    fun testSuccessResponse() {
        val result = sensorStatusCreateController.execute(baseForm)

        assertNotNull(result)
        assertTrue(result.data is Long)
        assertEquals(HttpStatus.OK, result.status)
        assertNull(result.errors)

        // Ensure a log record has been created
        assertEquals(1, logRepo.count())

        logRepo.findAll().forEach {
            assertEquals(HttpStatus.OK.value(), it.status)
        }
    }

    @Test
    fun testInvalidFormResponse() {
        // Copy of the form with a null gate value
        val formCopy = baseForm.copy(
                gate = null
        )

        val result = sensorStatusCreateController.execute(formCopy)

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
        // Copy of the form with a null gate value
        val formCopy = baseForm.copy(
                gate = 3
        )

        val result = sensorStatusCreateController.execute(formCopy)

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
}