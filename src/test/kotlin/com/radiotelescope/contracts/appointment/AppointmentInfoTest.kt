package com.radiotelescope.contracts.appointment

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class AppointmentInfoTest {
    private var startTime = Date(System.currentTimeMillis() + 10000L)
    private var endTime = Date(System.currentTimeMillis() + 30000L)

    @Test
    fun testPrimaryConstructor() {
        val appointmentInfo = AppointmentInfo(
                id = 1L,
                startTime = startTime,
                endTime = endTime,
                telescopeId = 1L,
                isPublic = true,
                userId = 1L,
                userFirstName = "Cody",
                userLastName = "Spath",
                status = Appointment.Status.SCHEDULED.label,
                rightAscension = 311.0,
                declination = 69.0
        )

        assertEquals(1L, appointmentInfo.id)
        assertEquals(startTime, appointmentInfo.startTime)
        assertEquals(endTime, appointmentInfo.endTime)
        assertEquals(1L, appointmentInfo.telescopeId)
        assertTrue(appointmentInfo.isPublic)
        assertEquals(1L, appointmentInfo.userId)
        assertEquals("Cody", appointmentInfo.userFirstName)
        assertEquals("Spath", appointmentInfo.userLastName)
        assertEquals(Appointment.Status.SCHEDULED.label, appointmentInfo.status)

        if (appointmentInfo.rightAscension == null || appointmentInfo.declination == null)
            fail("Should not be null")
        else {
            assertEquals(311.0, appointmentInfo.rightAscension!!, 0.00001)
            assertEquals(69.0, appointmentInfo.declination!!, 0.00001)
        }
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
                isPublic = true
        )

        val coordinate = Coordinate(
                rightAscension = 311.0,
                declination = 69.0
        )

        appointment.user = user
        appointment.id = 1L
        appointment.status = Appointment.Status.SCHEDULED
        appointment.coordinate = coordinate

        val appointmentInfo = AppointmentInfo(appointment)

        assertEquals(1L, appointmentInfo.id)
        assertEquals(startTime, appointmentInfo.startTime)
        assertEquals(endTime, appointmentInfo.endTime)
        assertEquals(1L, appointmentInfo.telescopeId)
        assertTrue(appointmentInfo.isPublic)
        assertEquals(1L, appointmentInfo.userId)
        assertEquals("Cody", appointmentInfo.userFirstName)
        assertEquals("Spath", appointmentInfo.userLastName)
        assertEquals(Appointment.Status.SCHEDULED.label, appointmentInfo.status)

        if (appointmentInfo.declination == null || appointmentInfo.rightAscension == null)
            fail("Should not be null")
        else {
            assertEquals(appointment.coordinate?.rightAscension!!, appointmentInfo.rightAscension!!, 0.00001)
            assertEquals(appointment.coordinate?.declination!!, appointmentInfo.declination!!, 0.00001)
        }
    }


}