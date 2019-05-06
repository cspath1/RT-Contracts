package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Abstract factory interface with methods for all [CelestialBody] operations
 */
interface CelestialBodyFactory {
    /**
     * Abstract command used to create a new [CelestialBody] object
     *
     * @param request the [Create.Request] request
     * @return a [Command] object
     */
    fun create(request: Create.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a [CelestialBody] object
     *
     * @param id the [CelestialBody] id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<CelestialBodyInfo, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve a list of [CelestialBody] objects
     *
     * @param pageable the [Pageable] object
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to search for [CelestialBody] objects
     *
     * @param searchCriteria the [SearchCriteria] object
     * @param pageable the [Pageable] interface
     * @return a [Command] object
     */
    fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable): Command<Page<CelestialBodyInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update an existing [CelestialBody] record
     *
     * @param request the [Update.Request] object
     * @return a [Command] object
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>
}