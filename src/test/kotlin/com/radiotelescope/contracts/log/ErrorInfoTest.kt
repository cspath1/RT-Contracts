package com.radiotelescope.contracts.log

import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.log.Log
import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpStatus
import java.util.*

internal class ErrorInfoTest {
    @Test
    fun testPrimaryConstructor() {
        val errorInfo = ErrorInfo(
                logId = 1L,
                field = "FIRST_NAME",
                message = "First name may not be blank"
        )

        assertEquals(1L, errorInfo.logId)
        assertEquals("FIRST_NAME", errorInfo.field)
        assertEquals("First name may not be blank", errorInfo.message)
    }

    @Test
    fun testSecondaryConstructor() {
        val log = Log(
                affectedTable = Log.AffectedTable.USER,
                action = "User Registration",
                timestamp = Date(),
                affectedRecordId = 1L,
                status = HttpStatus.OK.value()
        )

        log.isSuccess
        log.id = 1L

        val error = Error(
                log = log,
                field = "FIRST_NAME",
                message = "First name may not be blank"
        )

        val errorInfo = ErrorInfo(
                error = error,
                logId = log.id
        )

        assertEquals(error.log.id, errorInfo.logId)
        assertEquals(error.field, errorInfo.field)
        assertEquals(error.message, errorInfo.message)
    }
}