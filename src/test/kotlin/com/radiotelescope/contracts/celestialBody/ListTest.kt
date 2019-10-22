package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val pageable = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Create a few Celestial Bodies
        val coordinateOne = Coordinate(
                hours = 5,
                minutes = 34,
                seconds = 32,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 5,
                        minutes = 34
                ),
                declination = 22.0
        )
        coordinateRepo.save(coordinateOne)

        testUtil.createCelestialBody(
                name = "Crab Nebula",
                coordinate = coordinateOne
        )

        val coordinateTwo = Coordinate(
                hours = 14,
                minutes = 39,
                seconds = 37,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 14,
                        minutes = 39
                ),
                declination = -60.5
        )
        coordinateRepo.save(coordinateTwo)

        testUtil.createCelestialBody(
                name = "Alpha Centauri",
                coordinate = coordinateTwo
        )
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, errors) = List(
                pageable = pageable,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)

        assertEquals(2, page!!.content.size)
    }
}