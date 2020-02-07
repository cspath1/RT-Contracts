package com.radiotelescope.contracts.thresholds

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    @Before
    fun setUp() {
        // Persist the thresholds
        val thresholds = Thresholds(
                wind = 30,
                azMotorTemp = 80,
                elevMotorTemp = 80,
                azMotorVibration = 1,
                elevMotorVibration = 1,
                azMotorCurrent = 6,
                elevMotorCurrent = 6,
                counterBalanceVibration = 0.42
        )
        thresholdsRepo.save(thresholds)
    }

    @Test
    fun test_Success() {
        // Execute the command
        val (info, errors) = Retrieve(
                thresholdsRepo = thresholdsRepo
        ).execute()

        // Make sure it was a success
        Assert.assertNull(errors)
        Assert.assertNotNull(info)
    }
}