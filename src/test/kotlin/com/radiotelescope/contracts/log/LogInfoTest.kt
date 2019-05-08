package com.radiotelescope.contracts.log

import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import java.util.*

internal class LogInfoTest {
    private lateinit var theUser: User
    private var date = Date()

    @Before
    fun setUp() {
        theUser = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "HaDoPeLaGiC CrUsT"
        )
    }

    @Test
    fun testPrimaryConstructor() {
        val logInfo = LogInfo(
                id = 1L,
                userFirstName = "Cody",
                userLastName = "Spath",
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER.label,
                action = "User Registration",
                timestamp = date,
                isSuccess = true,
                status = HttpStatus.OK.value()
        )

        assertEquals(1L, logInfo.id)
        assertEquals("Cody", logInfo.userFirstName)
        assertEquals("Spath", logInfo.userLastName)
        assertEquals(1L, logInfo.affectedRecordId)
        assertEquals(Log.AffectedTable.USER.label, logInfo.affectedTable)
        assertEquals("User Registration", logInfo.action)
        assertEquals(date, logInfo.timestamp)
        assertEquals(HttpStatus.OK.value(), logInfo.status)
        assertTrue(logInfo.isSuccess)
    }

    @Test
    fun testFirstSecondaryConstructor() {
        val log = Log(
                action = "User Registration",
                timestamp = date,
                affectedRecordId = 1L,
                status = HttpStatus.OK.value()
        )

        log.affectedTable = Log.AffectedTable.USER
        log.isSuccess = true
        log.id = 1L

        val logInfo = LogInfo(log)

        assertEquals(log.id, logInfo.id)
        assertEquals(log.isSuccess, logInfo.isSuccess)
        assertEquals(log.affectedRecordId, logInfo.affectedRecordId)
        assertEquals(log.affectedTable!!.label, logInfo.affectedTable)
        assertEquals(log.action, logInfo.action)
        assertEquals(log.timestamp, logInfo.timestamp)
        assertEquals(HttpStatus.OK.value(), logInfo.status)
        assertNull(logInfo.userFirstName)
        assertNull(logInfo.userLastName)
    }

    @Test
    fun testSecondSecondaryConstructor() {
        val log = Log(
                action = "User Registration",
                timestamp = date,
                affectedRecordId = 1L,
                status = HttpStatus.OK.value()
        )

        log.affectedTable = Log.AffectedTable.USER
        log.isSuccess = true
        log.id = 1L

        val user = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "HaDoPeLaGiC CrUsT"
        )

        user.id = 1L
        log.user = user

        val logInfo = LogInfo(log, user)

        assertEquals(log.id, logInfo.id)
        assertEquals(log.isSuccess, logInfo.isSuccess)
        assertEquals(log.action, logInfo.action)
        assertEquals(log.affectedTable!!.label, logInfo.affectedTable)
        assertEquals(log.affectedRecordId, logInfo.affectedRecordId)
        assertEquals(log.timestamp, logInfo.timestamp)
        assertEquals(log.status, logInfo.status)
        assertEquals(user.firstName, logInfo.userFirstName)
        assertEquals(user.lastName, logInfo.userLastName)
    }
}