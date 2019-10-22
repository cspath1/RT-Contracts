package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.Filter
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SearchTest : AbstractSpringTest() {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val searchCriteria = SearchCriteria(Filter.NAME, value = "Crab")
    private lateinit var pageable: Pageable

    @Before
    fun setUp() {
        // Persist two different celestial bodies
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

        pageable = PageRequest.of(0, 25)
    }

    @Test
    fun testValidConstraints_Success() {
        val (page, errors) = Search(
                searchCriteria = listOf(searchCriteria),
                pageable = pageable,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        assertNotNull(page)
        assertNull(errors)

        // There should be one result
        assertEquals(1, page!!.content.size)
    }

    @Test
    fun testInvalidConstraints_NoSearchParameters_Failure() {
        val (page, errors) = Search(
                searchCriteria = listOf(),
                pageable = pageable,
                celestialBodyRepo = celestialBodyRepo
        ).execute()

        assertNull(page)
        assertNotNull(errors)

        // Make sure it failed for the suspected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }
}