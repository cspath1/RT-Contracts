package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.coordinate.Coordinate
import org.junit.Assert.*
import org.junit.Test

internal class CelestialBodyInfoTest {
    @Test
    fun testPrimaryConstructor() {
        val celestialBodyInfo = CelestialBodyInfo(
                id = 1L,
                name = "Crab Nebula",
                hours = 5,
                minutes = 34,
                seconds = 32,
                declination = 22.0
        )

        assertEquals(1L, celestialBodyInfo.id)
        assertEquals("Crab Nebula", celestialBodyInfo.name)
        assertEquals(5, celestialBodyInfo.hours)
        assertEquals(34, celestialBodyInfo.minutes)
        assertEquals(32, celestialBodyInfo.seconds)
        assertEquals(22.0, celestialBodyInfo.declination)
    }

    @Test
    fun testSecondaryConstructor() {
        val celestialBody = CelestialBody(name = "Crab Nebula")
        celestialBody.id = 1L
        val coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34
                ),
                declination = 22.0
        )
        celestialBody.coordinate = coordinate

        val celestialBodyInfo = CelestialBodyInfo(celestialBody)

        assertEquals(celestialBody.id, celestialBodyInfo.id)
        assertEquals(celestialBody.name, celestialBodyInfo.name)
        assertEquals(celestialBody.coordinate!!.hours, celestialBodyInfo.hours)
        assertEquals(celestialBody.coordinate!!.minutes, celestialBodyInfo.minutes)
        assertEquals(celestialBody.coordinate!!.seconds, celestialBodyInfo.seconds)
        assertEquals(celestialBody.coordinate!!.declination, celestialBodyInfo.declination)
    }
}