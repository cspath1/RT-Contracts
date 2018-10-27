package com.radiotelescope.contracts.log

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
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
internal class ListTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    private var pageable = PageRequest.of(0, 10)

    @Before
    fun setUp() {
        // Create a few logs
        for (i in 0..10) {
            testUtil.createLog(
                    userId = null,
                    affectedRecordId = i.toLong(),
                    affectedTable = Log.AffectedTable.USER,
                    action = "User Registration",
                    timestamp = Date(),
                    isSuccess = true
            )
        }
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = List(
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)
        assertEquals(10, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        logRepo.deleteAll()

        val (page, errors) = List(
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)
        assertEquals(0, page!!.content.size)
    }
}