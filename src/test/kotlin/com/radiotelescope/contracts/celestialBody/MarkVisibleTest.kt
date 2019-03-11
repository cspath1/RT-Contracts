package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.TestUtil
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
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class MarkVisibleTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var celestialBody: CelestialBody

    @Before
    fun setUp() {
        // Create a coordinate
        val coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34,
                        seconds = 32
                ),
                declination = 22.0
        )
        coordinateRepo.save(coordinate)

        // Create a Celestial Body object
        celestialBody = testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinate
        )
        celestialBody.status = CelestialBody.Status.HIDDEN
        celestialBodyRepo.save(celestialBody)
    }

    @Test
    fun testValidConstraints_Success() {
        val (id, errors) = MarkVisible(
                id = celestialBody.id,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure the status is now hidden
        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertEquals(CelestialBody.Status.VISIBLE, theCelestialBody.status)
    }

    @Test
    fun testInvalidId_Failure() {
        val (id, errors) = MarkVisible(
                id = 311L,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testAlreadyHidden_Failure() {
        celestialBody.status = CelestialBody.Status.VISIBLE
        celestialBodyRepo.save(celestialBody)

        val (id, errors) = MarkVisible(
                id = celestialBody.id,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the correct reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.STATUS].isNotEmpty())
    }
}