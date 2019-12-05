package com.radiotelescope.repository.sensorStatus

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
internal class SensorStatusTest : AbstractSpringTest() {
    @Autowired
    private lateinit var sensorStatusRepo: ISensorStatusRepository

    private lateinit var sensorStatus: SensorStatus

    @Before
    fun setUp() {
        testUtil.createSensorStatus(0, 0, 0, 0, 0)
        sensorStatus = testUtil.createSensorStatus(1, 1, 1, 1, 1)
    }

    @Test
    fun testGetMostRecent() {
        val sensorStatusGet = sensorStatusRepo.getMostRecentSensorStatus()
        assertEquals(sensorStatus.id, sensorStatusGet.id)
    }
}