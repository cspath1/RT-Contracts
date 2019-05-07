package com.radiotelescope.contracts.telescopeLog

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import com.radiotelescope.repository.telescopeLog.TelescopeLog
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD ,scripts = ["classpath:sql/seedTelescopeLog.sql"])
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    private lateinit var telescopeLog: TelescopeLog

    @Before
    fun setUp() {
        telescopeLog = telescopeLogRepo.findAll().first()
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command with a valid id
        val (info, errors) = Retrieve(
                telescopeLogId = telescopeLog.getId(),
                telescopeLogRepo = telescopeLogRepo
        ).execute()

        // Make sure it was a success
        assertNull(errors)
        assertNotNull(info)

        assertEquals(info!!.id, telescopeLog.getId())
        assertEquals(info.message, telescopeLog.getMessage())
        assertEquals(info.logger, telescopeLog.getLogger())
        assertEquals(info.logLevel, telescopeLog.getLogLevel())
        assertEquals(info.thread, telescopeLog.getThread())
        assertEquals(info.date, telescopeLog.getDate())
    }

    @Test
    fun testInvalidId_Failure() {
        // Execute the command with an id that does not exist
        val (info, errors) = Retrieve(
                telescopeLogId = 311L,
                telescopeLogRepo = telescopeLogRepo
        ).execute()

        // Make sure it was an error
        assertNotNull(errors)
        assertNull(info)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }
}