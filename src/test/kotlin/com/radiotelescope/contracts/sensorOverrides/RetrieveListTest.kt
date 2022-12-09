package com.radiotelescope.contracts.sensorOverrides

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    @Before
    fun init() {
        // populate the overrides table with the defaults
        testUtil.populateDefaultSensorOverrides()
    }

    @Test
    fun testDefaultValues_Success() {
        val (info, errors) = RetrieveList(
                sensorOverridesRepo = sensorOverridesRepo
        ).execute()

        assertNotNull(info)
        assertNull(errors)
        assertEquals(SensorOverrides.Name.values().size, info!!.size)
    }

    @Test
    fun testAddedValues_Success() {
        // update the "wind" record
        val windThresholdRecord = SensorOverrides(SensorOverrides.Name.GATE, true)
        sensorOverridesRepo.save(windThresholdRecord)

        val (info, errors) = RetrieveList(
                sensorOverridesRepo = sensorOverridesRepo
        ).execute()

        assertNotNull(info)
        assertNull(errors)
        assertEquals(SensorOverrides.Name.values().size, info!!.size)
        assertTrue(info.first().overridden)
    }
}