package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.Log
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class LogInfoTest {
    private var date = Date()

    @Test
    fun testPrimaryConstructor() {
        val logInfo = LogInfo(
                id = 1L,
                userId = 1L,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER,
                action = "User Registration",
                timestamp = date,
                isSuccess = true
        )

        assertEquals(1L, logInfo.id)
        assertEquals(1L, logInfo.userId)
        assertEquals(1L, logInfo.affectedRecordId)
        assertEquals(Log.AffectedTable.USER, logInfo.affectedTable)
        assertEquals("User Registration", logInfo.action)
        assertEquals(date, logInfo.timestamp)
        assertTrue(logInfo.isSuccess)
    }

    @Test
    fun testSecondaryConstructor() {
        val log = Log(
                affectedTable = Log.AffectedTable.USER,
                action = "User Registration",
                timestamp = date,
                affectedRecordId = 1L
        )

        log.isSuccess = true
        log.userId = 1L
        log.id = 1L

        val logInfo = LogInfo(log)

        assertEquals(log.id, logInfo.id)
        assertEquals(log.userId, logInfo.userId)
        assertEquals(log.isSuccess, logInfo.isSuccess)
        assertEquals(log.affectedRecordId, logInfo.affectedRecordId)
        assertEquals(log.affectedTable, logInfo.affectedTable)
        assertEquals(log.action, logInfo.action)
        assertEquals(log.timestamp, logInfo.timestamp)
    }
}