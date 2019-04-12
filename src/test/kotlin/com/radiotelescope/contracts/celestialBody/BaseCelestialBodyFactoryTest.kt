package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.Filter
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class BaseCelestialBodyFactoryTest : AbstractSpringTest() {
    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private lateinit var factory: CelestialBodyFactory

    @Before
    fun init() {
        // Instantiate the factory
        factory = BaseCelestialBodyFactory(
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo
        )
    }

    @Test
    fun create() {
        // Call the factory method
        val cmd = factory.create(
                request = Create.Request(
                        name = "Crab Nebula",
                        hours = 5,
                        minutes = 34,
                        seconds = 32,
                        declination = 22.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Create)
    }

    @Test
    fun retrieve() {
        // Call the factory method
        val cmd = factory.retrieve(
                id = 311L
        )

        // Ensure it is the correct command
        assertTrue(cmd is Retrieve)
    }

    @Test
    fun list() {
        // Call the factory method
        val cmd = factory.list(
                pageable = PageRequest.of(0, 15)
        )

        // Ensure it is the correct command
        assertTrue(cmd is List)
    }

    @Test
    fun markHidden() {
        // Call the factory method
        val cmd = factory.markHidden(
                id = 311L
        )

        // Ensure it is the correct commmand
        assertTrue(cmd is MarkHidden)
    }

    @Test
    fun markVisible() {
        // Call the factory method
        val cmd = factory.markVisible(
                id = 311L
        )

        // Ensure it is the correct command
        assertTrue(cmd is MarkVisible)
    }

    @Test
    fun search() {
        // Call the factory method
        val cmd = factory.search(
                searchCriteria = listOf(SearchCriteria(Filter.NAME, "Crab")),
                pageable = PageRequest.of(0, 15)
        )

        // Ensure it is the correct command
        assertTrue(cmd is Search)
    }

    @Test
    fun update() {
        val cmd = factory.update(
                request = Update.Request(
                        id = 311,
                        name = "Investigate 311",
                        hours = 3,
                        minutes = 11,
                        seconds = 311,
                        declination = 311.0
                )
        )

        // Ensure it is the correct command
        assertTrue(cmd is Update)
    }
}