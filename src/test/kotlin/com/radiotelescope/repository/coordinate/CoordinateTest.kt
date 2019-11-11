package com.radiotelescope.repository.coordinate

import org.junit.Assert.*
import org.junit.Test

internal class CoordinateTest {
    @Test
    fun testHoursMinutesSecondsToDegrees() {
        val rightAscensionDegrees = Coordinate.hoursMinutesSecondsToDegrees(
                hours = 12,
                minutes = 12
        )

        assertEquals(183.0, rightAscensionDegrees, 0.0001)
    }
}