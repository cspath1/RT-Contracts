package com.radiotelescope.contracts.log

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.IUserRepository
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
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class LogListTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private var pageable = PageRequest.of(0, 30)

    @Before
    fun setUp() {
        // Persist a user
        val theUser = testUtil.createUser("cspath1@ycp.edu")

        // Create a few logs without user ids
        for (i in 0..9) {
            testUtil.createLog(
                    userId = null,
                    affectedRecordId = i.toLong(),
                    affectedTable = Log.AffectedTable.USER,
                    action = "User Registration",
                    timestamp = Date(),
                    isSuccess = true
            )
        }

        // Create a few logs with user ids
        for (i in 0..9) {
            testUtil.createLog(
                    userId = theUser.id,
                    affectedRecordId = i.toLong(),
                    affectedTable = Log.AffectedTable.APPOINTMENT,
                    action = "Appointment Creation",
                    timestamp = Date(),
                    isSuccess = true
            )
        }
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = LogList(
                pageable = pageable,
                logRepo = logRepo,
                userRepo = userRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)
        assertEquals(20, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        logRepo.deleteAll()

        val (page, errors) = LogList(
                pageable = pageable,
                logRepo = logRepo,
                userRepo = userRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)
        assertEquals(0, page!!.content.size)
    }
}