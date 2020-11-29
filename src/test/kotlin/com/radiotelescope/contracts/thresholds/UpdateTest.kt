package com.radiotelescope.contracts.thresholds

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds
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
    private lateinit var thresholdsRepo: IThresholdsRepository

    @Before
    fun init() {
        // Persist the thresholds
        val sensorThreshold = Thresholds(
                sensorName = Thresholds.Name.WIND,
                maximum = 30.0
        )
        thresholdsRepo.save(sensorThreshold)
    }

    @Test
    fun test_Success() {
        val (info, errors) = Update(
                request = Update.Request(
                        sensorName = Thresholds.Name.WIND.toString(),
                        maximum = 40.0
                ),
                thresholdsRepo = thresholdsRepo
        ).execute()

        assertEquals(2, thresholdsRepo.findAll().count())

        print("\n" + thresholdsRepo.findAll() + "\n")

        // Make sure it was a success
        assertNull(errors)
        assertNotNull(info)
    }
}