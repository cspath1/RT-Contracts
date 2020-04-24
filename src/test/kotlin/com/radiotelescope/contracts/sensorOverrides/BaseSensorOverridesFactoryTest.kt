package com.radiotelescope.contracts.sensorOverrides

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseSensorOverridesFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    private lateinit var factory: BaseSensorOverridesFactory

    @Before
    fun init() {
        factory = BaseSensorOverridesFactory(
                sensorOverridesRepo = sensorOverridesRepo
        )
    }

    @Test
    fun updateSensorOverride() {
        val cmd = factory.update(
                sensorName = "Gate",
                overridden = true
        )

        assertTrue(cmd is Update)
    }

    @Test
    fun retrieveListOfSensorOverrides() {
        val cmd = factory.retrieveList()

        assertTrue(cmd is RetrieveList)
    }
}