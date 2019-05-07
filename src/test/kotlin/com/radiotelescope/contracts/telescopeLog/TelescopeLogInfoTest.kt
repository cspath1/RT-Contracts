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
import java.util.*

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD ,scripts = ["classpath:sql/seedTelescopeLog.sql"])
@DataJpaTest
@RunWith(SpringRunner::class)
internal class TelescopeLogInfoTest : AbstractSpringTest()  {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    private lateinit var theTelescopeLog: TelescopeLog
    private lateinit var date: Date

    @Before
    fun setUp() {
        theTelescopeLog = telescopeLogRepo.findAll().first()
        date = theTelescopeLog.getDate()
    }

    @Test
    fun testPrimaryConstructor() {
        val telescopeLogInfo = TelescopeLogInfo(
                id = 1L,
                date = date,
                logLevel = "Log Level",
                thread = "Thread",
                logger = "Logger",
                message = "Message"
        )

        assertEquals(1L, telescopeLogInfo.id)
        assertEquals(date, telescopeLogInfo.date)
        assertEquals("Log Level", telescopeLogInfo.logLevel)
        assertEquals("Thread", telescopeLogInfo.thread)
        assertEquals("Logger", telescopeLogInfo.logger)
        assertEquals("Message", telescopeLogInfo.message)
    }

    @Test
    fun testSecondaryConstructor() {
        val telescopeLogInfo = TelescopeLogInfo(
                telescopeLog = theTelescopeLog
        )

        assertEquals(theTelescopeLog.getId(), telescopeLogInfo.id)
        assertEquals(theTelescopeLog.getDate(), telescopeLogInfo.date)
        assertEquals(theTelescopeLog.getLogLevel(), telescopeLogInfo.logLevel)
        assertEquals(theTelescopeLog.getLogger(), telescopeLogInfo.logger)
        assertEquals(theTelescopeLog.getMessage(), telescopeLogInfo.message)
    }
}