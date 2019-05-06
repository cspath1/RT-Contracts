package com.radiotelescope.repository.log

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import com.radiotelescope.repository.model.log.LogSpecificationBuilder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class LogTest : AbstractSpringTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository


    private lateinit var log: Log
    @Before
    fun setUp(){
        val user = testUtil.createUser("rpim@ycp.edu")

        // Create a log
        // testUtil.createLog automatically set the status as HttpStatus.OK.values()
        log = testUtil.createLog(
                user = user,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER,
                action = "User Retrieval",
                timestamp = Date(System.currentTimeMillis()),
                isSuccess = true
        )

        // Create a log
        testUtil.createLog(
                user = testUtil.createUser("cspath1@ycp.edu"),
                affectedRecordId = 123L,
                affectedTable = Log.AffectedTable.APPOINTMENT,
                action = "blank",
                timestamp = Date(System.currentTimeMillis()),
                isSuccess = false
        ).status = HttpStatus.BAD_REQUEST.value()

    }

    @Test
    fun testSearchAction() {
        val searchCriteria = SearchCriteria(Filter.ACTION, "user")
        val specification = LogSpecificationBuilder().with(searchCriteria).build()

        val logList = logRepo.findAll(specification)

        assertNotNull(logList)
        assertEquals(1, logList.size)

        assertTrue(logList[0].action.contains(log.action))
    }

    @Test
    fun testSearchAffectedTable() {
        val searchCriteria = SearchCriteria(Filter.AFFECTED_TABLE, listOf(Log.AffectedTable.USER))
        val specification = LogSpecificationBuilder().with(searchCriteria).build()

        val logList = logRepo.findAll(specification)

        assertNotNull(logList)
        assertEquals(1, logList.size)

        assertEquals(log.affectedTable, logList[0].affectedTable)
    }

    @Test
    fun testSearchIsSuccess() {
        val searchCriteria = SearchCriteria(Filter.IS_SUCCESS, true)
        val specification = LogSpecificationBuilder().with(searchCriteria).build()

        val logList = logRepo.findAll(specification)

        assertNotNull(logList)
        assertEquals(1, logList.size)

        assertTrue(logList[0].isSuccess)
    }

    @Test
    fun testStatus() {
        val searchCriteria = SearchCriteria(Filter.STATUS, HttpStatus.OK.value())
        val specification = LogSpecificationBuilder().with(searchCriteria).build()

        val logList = logRepo.findAll(specification)

        assertNotNull(logList)
        assertEquals(1, logList.size)

        assertEquals(HttpStatus.OK.value(), logList[0].status)
    }
}