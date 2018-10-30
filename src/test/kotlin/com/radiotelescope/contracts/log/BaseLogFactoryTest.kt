package com.radiotelescope.contracts.log

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseLogFactoryTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

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
        assertTrue(cmd is LogList)
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
}