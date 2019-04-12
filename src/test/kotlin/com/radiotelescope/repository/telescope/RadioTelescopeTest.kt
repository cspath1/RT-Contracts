package com.radiotelescope.repository.telescope

import com.radiotelescope.AbstractSpringTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
internal class RadioTelescopeTest : AbstractSpringTest() {
    @Autowired
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Before
    fun setUp() {
        assertEquals(1, radioTelescopeRepo.count())
    }

    @Test
    fun testRetrieveTelescope() {
        val telescope = radioTelescopeRepo.findById(1)

        assertTrue(telescope.isPresent)
        assertEquals(1, telescope.get().getId())
        assertTrue(telescope.get().getOnline())
    }
}