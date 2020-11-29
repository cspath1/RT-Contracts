package com.radiotelescope.repository.coordinate

import org.junit.Assert.*
import org.junit.Test

internal class CoordinateTest {
    @Test
    fun testhoursMinutesToDegrees() {
        val rightAscensionDegrees = Coordinate.hoursMinutesToDegrees(
                hours = 12,
                minutes = 12
        )

        assertEquals(183.0, rightAscensionDegrees, 0.0001)
    }
}