package com.radiotelescope.repository.telescope

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class TelescopeTest {
    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Before
    fun setUp() {
        assertEquals(1, telescopeRepo.count())
    }

    @Test
    fun testRetrieveTelescope() {
        val telescope = telescopeRepo.findById(1)

        assertTrue(telescope.isPresent)
        assertEquals(1, telescope.get().getId())
        assertTrue(telescope.get().getOnline())
    }
}