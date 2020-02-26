package com.radiotelescope.contracts.appointment.info

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class CelestialBodyAppointmentInfoTest {
    private var startTime = Date(System.currentTimeMillis() + 10000L)
    private var endTime = Date(System.currentTimeMillis() + 30000L)

    @Test
    fun testPrimaryConstructor() {
        val info = CelestialBodyAppointmentInfo(
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
                type = Appointment.Type.CELESTIAL_BODY.label,
                priority = Appointment.Priority.PRIMARY.label,
                celestialBodyName = "Alpha Centauri",
                hours = 12,
                minutes = 12,
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0
        )

        assertEquals(1L, info.id)
        assertEquals(1L, info.telescopeId)
        assertTrue(info.isPublic)
        assertEquals(startTime, info.startTime)
        assertEquals(endTime, info.endTime)
        assertEquals(1L, info.userId)
        assertEquals("cspath1@ycp.edu", info.userEmail)
        assertEquals("Cody", info.userFirstName)
        assertEquals("Spath", info.userLastName)
        assertEquals(Appointment.Status.SCHEDULED.label, info.status)
        assertEquals(Appointment.Type.CELESTIAL_BODY.label, info.type)
        assertEquals(Appointment.Priority.PRIMARY.label, info.priority)

        val hoursMinutesSecondsInDegrees = Coordinate.hoursMinutesToDegrees(
                hours = 12,
                minutes = 12
        )

        assertEquals(hoursMinutesSecondsInDegrees, info.rightAscension)
        assertEquals(12, info.hours)
        assertEquals(12, info.minutes)
        assertEquals(69.0, info.declination)
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
                type = Appointment.Type.CELESTIAL_BODY
        )

        val celestialBody = CelestialBody(name = "Crab Nebula")
        celestialBody.coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 5,
                        minutes = 34
                ),
                declination = 22.0
        )

        appointment.user = user
        appointment.id = 1L
        appointment.status = Appointment.Status.SCHEDULED
        appointment.celestialBody = celestialBody

        val appointmentInfo = CelestialBodyAppointmentInfo(appointment)

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
        assertEquals(Appointment.Type.CELESTIAL_BODY.label, appointmentInfo.type)
        assertEquals(Appointment.Priority.PRIMARY.label, appointmentInfo.priority)
        assertEquals(appointment.celestialBody!!.coordinate!!.rightAscension, appointmentInfo.rightAscension)
        assertEquals(appointment.celestialBody!!.coordinate!!.declination, appointmentInfo.declination)
        assertEquals(appointment.celestialBody!!.coordinate!!.hours, appointmentInfo.hours)
        assertEquals(appointment.celestialBody!!.coordinate!!.minutes, appointmentInfo.minutes)
    }
}