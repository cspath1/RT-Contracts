package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.toInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface used to retrieve a page
 * of [CelestialBody] objects
 *
 * @param pageable the [Pageable] request
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class List(
        private val pageable: Pageable,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It will return a page of [CelestialBodyInfo]
     * objects based on the [pageable] specification.
     */
    override fun execute(): SimpleResult<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>> {
        val celestialBodyPage = celestialBodyRepo.findAll(pageable)

        val infoPage = celestialBodyPage.toInfoPage()

        return SimpleResult(infoPage, null)
    }
}