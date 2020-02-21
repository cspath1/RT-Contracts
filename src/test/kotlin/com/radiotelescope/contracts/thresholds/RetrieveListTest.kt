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
internal class RetrieveListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    @Before
    fun init() {
        // populate the thresholds table with the defaults
        testUtil.populateDefaultThresholds()
    }

    @Test
    fun testDefaultValues_Success() {
        val (info, errors) = RetrieveList(
                thresholdsRepo = thresholdsRepo
        ).execute()

        assertNotNull(info)
        assertNull(errors)
        assertEquals(8, info!!.size)
    }

    @Test
    fun testAddedValues_Success() {
        // update the "wind" record
        val windThresholdRecord = Thresholds(Thresholds.Name.WIND, 40.0)
        thresholdsRepo.save(windThresholdRecord)

        val (info, errors) = RetrieveList(
                thresholdsRepo = thresholdsRepo
        ).execute()

        assertNotNull(info)
        assertNull(errors)
        assertEquals(8, info!!.size)
        assertEquals(40.0, info.first().maximum, 0.01)
    }
}