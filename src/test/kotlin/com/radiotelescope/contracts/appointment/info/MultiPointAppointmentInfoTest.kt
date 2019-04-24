package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class MultiPointAppointmentInfoTest {
    private var startTime = Date(System.currentTimeMillis() + 10000L)
    private var endTime = Date(System.currentTimeMillis() + 30000L)

    @Test
    fun testPrimaryConstructor() {
        val info = MultiPointAppointmentInfo(
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
                type = Appointment.Type.RASTER_SCAN.label,
                coordinates = arrayListOf()
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
        assertEquals(Appointment.Type.RASTER_SCAN.label, info.type)
        assertEquals(0, info.coordinates.size)
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
                type = Appointment.Type.RASTER_SCAN
        )

        // Two coordinates
        val coordinateOne = Coordinate(
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

        coordinateOne.appointment = appointment
        appointment.coordinateList.add(coordinateOne)

        val coordinateTwo = Coordinate(
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 13,
                        minutes = 13,
                        seconds = 13
                ),
                declination = 70.0,
                hours = 13,
                minutes = 13,
                seconds = 13
        )

        coordinateTwo.appointment = appointment
        appointment.coordinateList.add(coordinateTwo)

        appointment.user = user
        appointment.id = 1L
        appointment.status = Appointment.Status.SCHEDULED

        val appointmentInfo = MultiPointAppointmentInfo(appointment)

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
        assertEquals(Appointment.Type.RASTER_SCAN.label, appointmentInfo.type)
        assertEquals(2, appointmentInfo.coordinates.size)
    }
}