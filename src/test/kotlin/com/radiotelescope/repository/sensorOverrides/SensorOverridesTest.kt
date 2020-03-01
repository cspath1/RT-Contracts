package com.radiotelescope.repository.sensorOverrides

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
internal class SensorOverridesTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    @Before
    fun init() {
        testUtil.populateDefaultSensorOverrides()
    }

    @Test
    fun testMostRecentSensorOverridesByNameDefaults_Success() {
        val sensorOverride = sensorOverridesRepo.getMostRecentSensorOverridesByName(SensorOverrides.Name.GATE.toString())

        assertNotNull(sensorOverride)
        assertEquals(SensorOverrides.Name.GATE, sensorOverride.sensorName)
        assertEquals(false, sensorOverride.overridden)
    }

    @Test
    fun testGetMostRecentSensorOverrideByNameNewRecord_Success() {
        val newSensorOverride = SensorOverrides(SensorOverrides.Name.GATE, true)
        sensorOverridesRepo.save(newSensorOverride)

        val sensorOverride = sensorOverridesRepo.getMostRecentSensorOverridesByName(SensorOverrides.Name.GATE.toString())

        assertNotNull(sensorOverride)
        assertEquals(SensorOverrides.Name.GATE, sensorOverride.sensorName)
        assertEquals(true, sensorOverride.overridden)
    }
}