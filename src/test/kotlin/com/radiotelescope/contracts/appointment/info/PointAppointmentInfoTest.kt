package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class PointAppointmentInfoTest {
    private var startTime = Date(System.currentTimeMillis() + 10000L)
    private var endTime = Date(System.currentTimeMillis() + 30000L)

    @Test
    fun testPrimaryConstructor() {
        val info = PointAppointmentInfo(
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
                type = Appointment.Type.POINT.label,
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
        assertEquals(Appointment.Type.POINT.label, info.type)

        val hoursMinutesSecondsInDegrees = Coordinate.hoursMinutesSecondsToDegrees(
                hours = 12,
                minutes = 12,
                seconds = 12
        )

        assertEquals(hoursMinutesSecondsInDegrees, info.rightAscension, 0.00001)
        assertEquals(12, info.hours)
        assertEquals(12, info.minutes)
        assertEquals(12, info.seconds)
        assertEquals(69.0, info.declination, 0.00001)
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
                type = Appointment.Type.POINT
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
        appointment.coordinateList = mutableListOf()
        appointment.coordinateList.add(coordinate)

        val appointmentInfo = PointAppointmentInfo(appointment)

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
        assertEquals(Appointment.Type.POINT.label, appointmentInfo.type)
        assertEquals(appointment.coordinateList[0].rightAscension, appointmentInfo.rightAscension, 0.00001)
        assertEquals(appointment.coordinateList[0].declination, appointmentInfo.declination, 0.00001)
        assertEquals(appointment.coordinateList[0].hours, appointmentInfo.hours)
        assertEquals(appointment.coordinateList[0].minutes, appointmentInfo.minutes)
        assertEquals(appointment.coordinateList[0].seconds, appointmentInfo.seconds)
    }
}