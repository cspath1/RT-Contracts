package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBody: CelestialBody

    @Before
    fun setUp() {
        // Persist a coordinate
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
        coordinateRepo.save(coordinate)

        // Persist a Celestial Body
        celestialBody = testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinate
        )
    }

    @Test
    fun testValidConstraints_Success() {
        // Execute the command with a valid id
        val (info, errors) = Retrieve(
                id = celestialBody.id,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it was a success
        assertNull(errors)
        assertNotNull(info)
    }

    @Test
    fun testInvalidId_Failure() {
        // Execute the command with an invalid id
        val (info, errors) = Retrieve(
                id = 311L,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it was a failure
        assertNotNull(errors)
        assertNull(info)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }
}