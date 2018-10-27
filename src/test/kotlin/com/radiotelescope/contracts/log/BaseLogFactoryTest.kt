package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
class BaseLogFactoryTest {
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
}