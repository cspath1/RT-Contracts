package com.radiotelescope.controller.weatherData

import com.radiotelescope.controller.model.weatherData.CreateForm
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
internal class WeatherDataCreateControllerTest : BaseWeatherDataRestControllerTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var weatherDataCreateController: WeatherDataCreateController

    private val baseForm = CreateForm(
            windSpeed = 100.0000f,
            windDirectionDeg = 100.0000f,
            windDirectionStr = "N",
            outsideTemperatureDegF = 100.0000f,
            insideTemperatureDegF = 100.0000f,
            rainRate = 100.0000f,
            rainTotal = 100.0000f,
            rainDay = 100.0000f,
            rainMonth = 100.0000f,
            barometricPressure = 100.0000f,
            dewPoint = 100.0000f,
            windChill = 100.0000f,
            humidity = 100.0000f,
            heatIndex = 100.0000f
    )

    @Before
    override fun init() {
        super.init()

        weatherDataCreateController = WeatherDataCreateController(
                weatherDataWrapper = getWrapper(),
                logger = getLogger()
        )
    }

    @Test
    fun testSuccessResponse() {
        val result = weatherDataCreateController.execute(baseForm)

        assertNotNull(result)
        //assertTrue(result.data is Long)
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
        // Copy of the form with a null value
        val formCopy = baseForm.copy(
                windSpeed = null
        )

        val result = weatherDataCreateController.execute(formCopy)

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