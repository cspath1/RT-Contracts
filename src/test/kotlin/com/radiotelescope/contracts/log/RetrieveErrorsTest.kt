package com.radiotelescope.contracts.log

import com.google.common.collect.HashMultimap
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.user.ErrorTag
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class RetrieveErrorsTest {
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
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    private lateinit var theLog: Log

    @Before
    fun setUp() {
        // Create an error map
        val errors = HashMultimap.create<ErrorTag, String>()

        errors.put(ErrorTag.FIRST_NAME, "First Name may not be blank")
        errors.put(ErrorTag.EMAIL, "Email is already in use")

        // Persist an error log
        theLog = testUtil.createErrorLog(
                user = null,
                affectedRecordId = null,
                affectedTable = Log.AffectedTable.USER,
                action = "User Registration",
                timestamp = Date(),
                isSuccess = false,
                errors = errors.toStringMap()
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (list, errors) = RetrieveErrors(
                logId = theLog.id,
                logRepo = logRepo
        ).execute()

        assertNull(errors)
        assertNotNull(list)
        assertEquals(2, list!!.size)
    }

    @Test
    fun testInvalidId_Failure() {
        val (list, errors) = RetrieveErrors(
                logId = 311L,
                logRepo = logRepo
        ).execute()

        assertNotNull(errors)
        assertNull(list)
        assertTrue(errors!![com.radiotelescope.contracts.log.ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testLogIsSuccess_Failure() {
        // Delete the errors and set the success
        // flag to true
        errorRepo.deleteAll()

        theLog.isSuccess = true
        logRepo.save(theLog)

        val (list, errors) = RetrieveErrors(
                logId = theLog.id,
                logRepo = logRepo
        ).execute()

        assertNotNull(errors)
        assertNull(list)
        assertTrue(errors!![com.radiotelescope.contracts.log.ErrorTag.SUCCESS].isNotEmpty())
    }
}