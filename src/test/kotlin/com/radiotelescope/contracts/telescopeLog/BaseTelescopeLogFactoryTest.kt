package com.radiotelescope.contracts.telescopeLog

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseTelescopeLogFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    private lateinit var factory: TelescopeLogFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseTelescopeLogFactory(telescopeLogRepo)
    }

    @Test
    fun retrieve() {
        // Call the factory method
        val cmd = factory.retrieve(311L)

        // Ensure it is the correct command
        assertTrue(cmd is Retrieve)
    }

    @Test
    fun list() {
        // Call the factory method
        val cmd = factory.list(PageRequest.of(0, 5))

        // Ensure it is the correct command
        assertTrue(cmd is List)
    }
}