package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class DriftScanAppointmentInfoTest {
    private var startTime = Date(System.currentTimeMillis() + 10000L)
    private var endTime = Date(System.currentTimeMillis() + 30000L)

    @Test
    fun testPrimaryConstructor() {
        val info = DriftScanAppointmentInfo(
                id = 1L,
                startTime = startTime,
                endTime = endTime,
                telescopeId = 1L,
                isPublic = true,
                userId = 1L,
                userFirstName = "Cody",
                userLastName = "Spath",
                userEmail = "cspath1@ycp.edu",
                status = Appointment.Status.SCHEDULED.label,
                type = Appointment.Type.DRIFT_SCAN.label,
                priority = Appointment.Priority.PRIMARY.label,
                azimuth = 311.0,
                elevation = 45.0
        )

        assertEquals(1L, info.id)
        assertEquals(startTime, info.startTime)
        assertEquals(endTime, info.endTime)
        assertEquals(1L, info.telescopeId)
        assertTrue(info.isPublic)
        assertEquals(1L, info.userId)
        assertEquals("Cody", info.userFirstName)
        assertEquals("Spath", info.userLastName)
        assertEquals("cspath1@ycp.edu", info.userEmail)
        assertEquals(Appointment.Status.SCHEDULED.label, info.status)
        assertEquals(Appointment.Type.DRIFT_SCAN.label, info.type)
        assertEquals(Appointment.Priority.PRIMARY.label, info.priority)
        assertEquals(info.azimuth, 311.0, 0.0001)
        assertEquals(info.elevation, 45.0, 0.0001)
    }

    @Test
    fun testSecondaryConstructor() {
        val user = User(
                firstName = "Cody",
                lastName = "Spath",
                email = "cspath1@ycp.edu",
                password = "Password"
        )

        user.id = 1L

        val appointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = 1L,
                isPublic = true,
                priority = Appointment.Priority.PRIMARY,
                type = Appointment.Type.DRIFT_SCAN
        )

        val orientation = Orientation(
                azimuth = 311.0,
                elevation = 45.0
        )

        appointment.user = user
        appointment.id = 1L
        appointment.status = Appointment.Status.SCHEDULED
        appointment.orientation = orientation

        val appointmentInfo = DriftScanAppointmentInfo(appointment)

        assertEquals(1L, appointmentInfo.id)
        assertEquals(startTime, appointmentInfo.startTime)
        assertEquals(endTime, appointmentInfo.endTime)
        assertEquals(1L, appointmentInfo.telescopeId)
        assertTrue(appointmentInfo.isPublic)
        assertEquals(1L, appointmentInfo.userId)
        assertEquals("Cody", appointmentInfo.userFirstName)
        assertEquals("Spath", appointmentInfo.userLastName)
        assertEquals("cspath1@ycp.edu", appointmentInfo.userEmail)
        assertEquals(Appointment.Status.SCHEDULED.label, appointmentInfo.status)
        assertEquals(Appointment.Type.DRIFT_SCAN.label, appointmentInfo.type)
        assertEquals(Appointment.Priority.PRIMARY.label, appointmentInfo.priority)
        assertEquals(appointment.orientation!!.azimuth, appointmentInfo.azimuth, 0.00001)
        assertEquals(appointment.orientation!!.elevation, appointmentInfo.elevation, 0.00001)
    }
}