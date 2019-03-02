package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.celestialBody.CelestialBody

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
}