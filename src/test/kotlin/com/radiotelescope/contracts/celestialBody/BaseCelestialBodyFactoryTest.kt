package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.Filter
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class BaseCelestialBodyFactoryTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

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
                searchCriteria = SearchCriteria(Filter.NAME, "Crab"),
                pageable = PageRequest.of(0, 15)
        )

        // Ensure it is the correct command
        assertTrue(cmd is Search)
    }
}