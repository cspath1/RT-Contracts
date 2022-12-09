package com.radiotelescope.repository.acceleration

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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedAccelerationBlob.sql"])
internal class AccelerationBlobTest : AbstractSpringTest() {
    @Autowired
    private lateinit var AzimuthAccelerationBlob: IAzimuthAccelerationBlobRepository
    @Autowired
    private lateinit var ElevationAccelerationBlob: IElevationAccelerationBlobRepository
    @Autowired
    private lateinit var CounterbalanceAccelerationBlob: ICounterbalanceAccelerationBlobRepository

    @Before
    fun setUp() {
        assertEquals(1, AzimuthAccelerationBlob.count())
        assertEquals(1, ElevationAccelerationBlob.count())
        assertEquals(1, CounterbalanceAccelerationBlob.count())
    }

    @Test
    fun testRetrieveAzimuthAccelerationBlob() {
        val acceleration = AzimuthAccelerationBlob.findById(1)

        assertTrue(acceleration.isPresent)
        assertEquals(1, acceleration.get().getId())
    }

    @Test
    fun testRetrieveElevationAccelerationBlob() {
        val acceleration = ElevationAccelerationBlob.findById(1)

        assertTrue(acceleration.isPresent)
        assertEquals(1, acceleration.get().getId())
    }

    @Test
    fun testRetrieveCounterbalanceAccelerationBlob() {
        val acceleration = CounterbalanceAccelerationBlob.findById(1)

        assertTrue(acceleration.isPresent)
        assertEquals(1, acceleration.get().getId())
    }
}