package com.radiotelescope.contracts.thresholds

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseThresholdsFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    private lateinit var factory: BaseThresholdsFactory

    @Before
    fun init() {
        factory = BaseThresholdsFactory(
                thresholdsRepo = thresholdsRepo
        )
    }

    @Test
    fun retrieveThreshold() {
        val cmd = factory.retrieve(
                sensorName = "WIND"
        )

        assertTrue(cmd is Retrieve)
    }

    @Test
    fun retrieveListOfThresholds() {
        val cmd = factory.retrieveList()

        assertTrue(cmd is RetrieveList)
    }

    @Test
    fun updateThreshold() {
        val cmd = factory.update(
                sensorName = "WIND",
                maximum = 1.0
        )

        assertTrue(cmd is Update)
    }
}