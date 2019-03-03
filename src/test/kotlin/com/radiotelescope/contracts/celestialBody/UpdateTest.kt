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
internal class UpdateTest {
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

    private val baseRequest = Update.Request(
            id = -1L,
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    private lateinit var crabNebula: CelestialBody
    private lateinit var theSun: CelestialBody

    @Before
    fun setUp() {
        val coordinate = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34,
                        seconds = 32
                ),
                declination = 22.1
        )
        coordinateRepo.save(coordinate)

        crabNebula = testUtil.createCelestialBody(
                name = "The Crab Nebula",
                coordinate = coordinate
        )

        theSun = testUtil.createCelestialBody(
                name = "The Sun",
                coordinate = null
        )
    }

    @Test
    fun testValidConstraints_CoordinateToCoordinate_Success() {
        // Test the scenario where the original celestial body had
        // a coordinate, and the updated entity needs one as well
        val requestCopy = baseRequest.copy(id = crabNebula.id)

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure the coordinate was updated correctly
        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertNotNull(theCelestialBody.coordinate)
        assertEquals(requestCopy.declination, theCelestialBody.coordinate!!.declination)
    }

    @Test
    fun testValidConstraints_NoCoordinateToCoordinate_Success() {
        // Test the scenario where the original celestial body did not
        // have a coordinate, but the updated entity needs one
        val requestCopy = baseRequest.copy(id = theSun.id)

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure a coordinate was persisted for this celestial body
        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertEquals(requestCopy.name, theCelestialBody.name)
        assertNotNull(theCelestialBody.coordinate)

        // Make sure all of the coordinate information is correct
        assertEquals(requestCopy.hours, theCelestialBody.coordinate!!.hours)
        assertEquals(requestCopy.minutes, theCelestialBody.coordinate!!.minutes)
        assertEquals(requestCopy.seconds, theCelestialBody.coordinate!!.seconds)
        assertEquals(requestCopy.declination, theCelestialBody.coordinate!!.declination)
        val rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                hours = requestCopy.hours!!,
                minutes = requestCopy.minutes!!,
                seconds = requestCopy.seconds!!
        )
        assertEquals(rightAscension, theCelestialBody.coordinate!!.rightAscension, 0.00001)
    }

    @Test
    fun testValidConstraints_CoordinateToNoCoordinate_Success() {
        // Test the scenario where the original celestial body had a coordinate,
        // but the updated one will not
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                name = "Mars",
                hours = null,
                minutes = null,
                seconds = null,
                declination = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // There should no longer be a coordinate associated with this
        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertEquals(requestCopy.name, theCelestialBody.name)
        assertNull(theCelestialBody.coordinate)
    }

    @Test
    fun testValidConstraints_NoCoordinateToNoCoordinate_Success() {
        // Test the scenario where the original celestial body did not have a
        // coordinate, and will not have one when update
        val requestCopy = baseRequest.copy(
                id = theSun.id,
                name = "Sun",
                hours = null,
                minutes = null,
                seconds = null,
                declination = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        assertNotNull(id)
        assertNull(errors)

        // There should still be no coordinate associated
        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertEquals(requestCopy.name, theCelestialBody.name)
        assertNull(theCelestialBody.coordinate)
    }

    @Test
    fun testInvalidConstraints_InvalidId_Failure() {
        val (id, errors) = Update(
                request = baseRequest,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.ID].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_BlankName_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                name = ""
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NameTooLong_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                name = "Crab Nebula".repeat(30)
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullHours_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                hours = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_HoursBelowZero_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                hours = -1
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_HoursAboveTwentyThree_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                hours = 24
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullMinutes_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                minutes = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_MinutesBelowZero_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                minutes = -1
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_MinutesAboveFiftyNine_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                minutes = 60
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullSeconds_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                seconds = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_SecondsBelowZero_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                seconds = -1
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_SecondsAboveFiftyNine_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                seconds = 60
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullDeclination_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                declination = null
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_DeclinationBelowNegativeNinety_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                declination = -90.1
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_DeclinationAboveNinety_Failure() {
        val requestCopy = baseRequest.copy(
                id = crabNebula.id,
                declination = 90.1
        )

        val (id, errors) = Update(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure it failed
        assertNull(id)
        assertNotNull(errors)

        // Make sure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }
}