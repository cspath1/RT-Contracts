package com.radiotelescope.repository.thresholds

import com.radiotelescope.AbstractSpringTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ThresholdsTest : AbstractSpringTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    @Before
    fun init() {
        testUtil.populateDefaultThresholds()
    }

    @Test
    fun testGetMostRecentThresholdByNameDefaults_Success() {
        val sensorThreshold = thresholdsRepo.getMostRecentThresholdByName(Thresholds.Name.WIND.toString())

        assertNotNull(sensorThreshold)
        assertEquals(Thresholds.Name.WIND, sensorThreshold.sensorName)
        assertEquals(30.0, sensorThreshold.maximum, 0.01)
    }

    @Test
    fun testGetMostRecentThresholdByNameNewRecord_Success() {
        val newSensorThreshold = Thresholds(Thresholds.Name.WIND, 40.0)
        thresholdsRepo.save(newSensorThreshold)

        val sensorThreshold = thresholdsRepo.getMostRecentThresholdByName(Thresholds.Name.WIND.toString())

        assertNotNull(sensorThreshold)
        assertEquals(Thresholds.Name.WIND, sensorThreshold.sensorName)
        assertEquals(40.0, sensorThreshold.maximum, 0.01)
    }
}