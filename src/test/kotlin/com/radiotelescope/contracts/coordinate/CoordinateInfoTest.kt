package com.radiotelescope.contracts.coordinate

import com.radiotelescope.repository.coordinate.Coordinate
import org.junit.Assert.*
import org.junit.Test

internal class CoordinateInfoTest {
    @Test
    fun testPrimaryConstructor() {
        val info = CoordinateInfo(
                hours = 12,
                minutes = 12,
                seconds = 12,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0
        )

        val hoursMinutesSecondsInDegrees = Coordinate.hoursMinutesSecondsToDegrees(
                hours = 12,
                minutes = 12
        )

        assertEquals(hoursMinutesSecondsInDegrees, info.rightAscension, 0.00001)
        assertEquals(12, info.hours)
        assertEquals(12, info.minutes)
        assertEquals(12, info.seconds)
        assertEquals(69.0, info.declination, 0.00001)
    }

    @Test
    fun testSecondaryConstructor() {
        val coordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12,
                seconds = 12
        )

        val info = CoordinateInfo(coordinate)

        assertEquals(info.rightAscension, coordinate.rightAscension, 0.00001)
        assertEquals(info.declination, coordinate.declination, 0.00001)
        assertEquals(info.hours, coordinate.hours)
        assertEquals(info.minutes, coordinate.minutes)
        assertEquals(info.seconds, coordinate.seconds)
    }
}