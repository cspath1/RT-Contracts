package com.radiotelescope.repository.sensorNetworkConfig

import com.radiotelescope.AbstractSpringTest
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedSensorNetworkConfig.sql"])
internal class SensorNetworkConfigTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorNetworkConfigRepo: ISensorNetworkConfigRepository

    @Before
    fun setUp() {
        assertEquals(1, sensorNetworkConfigRepo.count())
    }

    @Test
    fun testRetrieveSensorNetworkConfig() {
        val sensorNetworkConfig = sensorNetworkConfigRepo.findById(1)

        assertTrue(sensorNetworkConfig.isPresent)
        assertEquals(1, sensorNetworkConfig.get().getId())
    }
}