package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.SolarSystemBodies
import org.junit.Assert.*
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
internal class CoordinateCreateTest {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val baseRequest = Create.Request(
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    @Test
    fun testValidConstraints_Coordinate_Success() {
        val (id, errors) = Create(
                request = baseRequest,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // And make sure everything was persisted
        assertEquals(1, celestialBodyRepo.count())
        assertEquals(1, coordinateRepo.count())

        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertNotNull(theCelestialBody.coordinate)
    }

    @Test
    fun testValidConstraints_SolarSystemBody_NoCoordinate_Success() {
        val requestCopy = baseRequest.copy(
                name = "Sun",
                hours = null,
                minutes = null,
                seconds = null,
                declination = null
        )

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure a CelestialBody object was persisted
        // without a coordinate
        assertEquals(1, celestialBodyRepo.count())
        assertEquals(0, coordinateRepo.count())

        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertNull(theCelestialBody.coordinate)
    }

    @Test
    fun testValidConstraints_SolarSystemBody_CoordinateNotPersisted_Success() {
        val requestCopy = baseRequest.copy(name = "Sun")

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a success
        assertNotNull(id)
        assertNull(errors)

        // Make sure that only a CelestialBody object was persisted
        // despite having coordinate information
        assertEquals(1, celestialBodyRepo.count())
        assertEquals(0, coordinateRepo.count())

        val theCelestialBody = celestialBodyRepo.findById(id!!).get()
        assertNull(theCelestialBody.coordinate)
    }

    @Test
    fun testInvalidConstraints_NullHours_Failure() {
        val requestCopy = baseRequest.copy(hours = null)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_HoursBelowZero_Failure() {
        val requestCopy = baseRequest.copy(hours = -1)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_HoursAboveTwentyThree_Failure() {
        val requestCopy = baseRequest.copy(hours = 24)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.HOURS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullMinutes_Failure() {
        val requestCopy = baseRequest.copy(minutes = null)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_MinutesBelowZero_Failure() {
        val requestCopy = baseRequest.copy(minutes = -1)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_MinutesAboveFiftyNine_Failure() {
        val requestCopy = baseRequest.copy(minutes = 60)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.MINUTES].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullSeconds_Failure() {
        val requestCopy = baseRequest.copy(seconds = null)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_SecondsBelowZero_Failure() {
        val requestCopy = baseRequest.copy(seconds = -1)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_SecondsAboveFiftyNine_Failure() {
        val requestCopy = baseRequest.copy(seconds = 60)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SECONDS].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NullDeclination_Failure() {
        val requestCopy = baseRequest.copy(declination = null)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_DeclinationBelowNegativeNinety_Failure() {
        val requestCopy = baseRequest.copy(declination = -91.00)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_DeclinationAboveNinety_Failure() {
        val requestCopy = baseRequest.copy(declination = 91.0)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.DECLINATION].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_BlankName_Failure() {
        val requestCopy = baseRequest.copy(name = " ")

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testInvalidConstraints_NameOverOneFifty_Failure() {
        val name = "The Sun".repeat(50)
        val requestCopy = baseRequest.copy(name = name)

        val (id, errors) = Create(
                request = requestCopy,
                coordinateRepo = coordinateRepo,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        // Make sure the command was a failure
        assertNotNull(errors)
        assertNull(id)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.NAME].isNotEmpty())
    }

    @Test
    fun testEachSolarSystemBody_NoCoordinate_Success() {
        SolarSystemBodies.values().forEach {
            val requestCopy = baseRequest.copy(name = it.label)

            val (id, errors) = Create(
                    request = requestCopy,
                    coordinateRepo = coordinateRepo,
                    celestialBodyRepo = celestialBodyRepo
            ).execute()

            assertNull(errors)
            assertNotNull(id)

            // Make sure the Celestial Body was persisted
            // without a Coordinate
            val theCelestialBody = celestialBodyRepo.findById(id!!).get()
            assertNull(theCelestialBody.coordinate)
        }
    }
}