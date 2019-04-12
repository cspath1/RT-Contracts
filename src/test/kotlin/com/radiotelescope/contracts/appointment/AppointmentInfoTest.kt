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
                userEmail = "cspath1@ycp.edu",
                status = Appointment.Status.SCHEDULED.label,
                hours = 12,
                minutes = 12,
                seconds = 12,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 12,
                        minutes = 12,
                        seconds = 12
                ),
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
        assertEquals("cspath1@ycp.edu", appointmentInfo.userEmail)
        assertEquals(Appointment.Status.SCHEDULED.label, appointmentInfo.status)

        if (appointmentInfo.rightAscension == null || appointmentInfo.declination == null)
            fail("Should not be null")
        else {
            val hoursMinutesSecondsInDegrees = Coordinate.hoursMinutesSecondsToDegrees(
                    hours = 12,
                    minutes = 12,
                    seconds = 12
            )
            assertEquals(hoursMinutesSecondsInDegrees, appointmentInfo.rightAscension!!, 0.00001)
            assertEquals(12, appointmentInfo.hours)
            assertEquals(12, appointmentInfo.minutes)
            assertEquals(12, appointmentInfo.seconds)
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
                isPublic = true,
                priority = Appointment.Priority.PRIMARY
        )

        val coordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 12,
                        minutes = 12,
                        seconds = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12,
                seconds = 12
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
        assertEquals("cspath1@ycp.edu", appointmentInfo.userEmail)
        assertEquals(Appointment.Status.SCHEDULED.label, appointmentInfo.status)

        if (appointmentInfo.declination == null || appointmentInfo.rightAscension == null)
            fail("Should not be null")
        else {

            assertEquals(appointment.coordinate?.rightAscension!!, appointmentInfo.rightAscension!!, 0.00001)
            assertEquals(appointment.coordinate?.declination!!, appointmentInfo.declination!!, 0.00001)
            assertEquals(appointment.coordinate?.hours, appointmentInfo.hours)
            assertEquals(appointment.coordinate?.minutes, appointmentInfo.minutes)
            assertEquals(appointment.coordinate?.seconds, appointmentInfo.seconds)
        }
    }


}