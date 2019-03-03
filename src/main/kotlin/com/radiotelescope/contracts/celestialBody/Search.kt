package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.model.celestialBody.CelestialBodySpecificationBuilder
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import com.radiotelescope.toInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used for Celestial Body searching
 *
 * @param searchCriteria the [SearchCriteria]
 * @param pageable the [Pageable] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class Search(
        private val searchCriteria: SearchCriteria,
        private val pageable: Pageable,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that will search for the Celestial Body records
     * based on the user's specification.
     */
    override fun execute(): SimpleResult<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>> {
        // Build the specification
        val specificationBuilder = CelestialBodySpecificationBuilder()
        val specification = specificationBuilder.with(searchCriteria).build()

        // Make the call using the specification
        val celestialBodyPage = celestialBodyRepo.findAll(specification, pageable)

        // Adapt that Celestial Body page into an info page
        val infoPage = celestialBodyPage.toInfoPage()

        return SimpleResult(infoPage, null)
    }
}