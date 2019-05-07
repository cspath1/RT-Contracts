package com.radiotelescope.contracts.log

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseLogFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var factory: LogFactory

    @Before
    fun init() {
        factory = BaseLogFactory(
                logRepo = logRepo,
                userRepo = userRepo
        )
    }

    @Test
    fun list() {
        // Call the factory method
        val cmd = factory.list(
                pageable = PageRequest.of(0, 5)
        )

        // Ensure it is the correct command
        assertTrue(cmd is List)
    }

    @Test
    fun retrieveErrors() {
        // Call the factory method
        val cmd = factory.retrieveErrors(
                logId = 1L
        )

        // Ensure it is the correct command
        assertTrue(cmd is RetrieveErrors)
    }

    @Test
    fun search() {
        // Call the factory method
        val cmd = factory.search(
                searchCriteria = listOf(SearchCriteria(Filter.ACTION, "Test")),
                pageable = PageRequest.of(0, 5)
        )

        // Ensure it is the correct command
        assertTrue(cmd is Search)
    }
}