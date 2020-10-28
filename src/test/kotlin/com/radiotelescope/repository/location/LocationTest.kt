package com.radiotelescope.repository.location

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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedLocation.sql"])
internal class LocationTest : AbstractSpringTest() {
    @Autowired
    private lateinit var locationRepo: ILocationRepository

    @Before
    fun setUp() {
        assertEquals(1, locationRepo.count())
    }

    @Test
    fun testRetrieveLocation() {
        val location = locationRepo.findById(1)

        assertTrue(location.isPresent)
        assertEquals(1, location.get().getId())
    }
}