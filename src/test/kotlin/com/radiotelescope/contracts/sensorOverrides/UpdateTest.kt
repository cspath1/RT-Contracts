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
internal class UpdateTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    @Before
    fun init() {
        // Persist the sensor override
        val sensorOverride = SensorOverrides(
                sensorName = SensorOverrides.Name.GATE,
                overridden = false
        )

        sensorOverridesRepo.save(sensorOverride)
    }

    @Test
    fun test_Success() {
        val (info, errors) = Update(
                request = Update.Request(
                        sensorName = SensorOverrides.Name.GATE.toString(),
                        overridden = true
                ),
                sensorOverridesRepo = sensorOverridesRepo
        ).execute()

        assertEquals(2, sensorOverridesRepo.findAll().count())

        // Make sure it was a success
        assertNull(errors)
        assertNotNull(info)
    }
}