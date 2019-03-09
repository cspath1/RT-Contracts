package com.radiotelescope.repository.model.celestialBody

import com.radiotelescope.repository.celestialBody.CelestialBody

/**
 * Data class containing the [Filter] - the field - of a [CelestialBody] entity.
 * This is what field will be used to search
 *
 * @param filter the [Filter] enum
 * @value the Search value
 */
data class SearchCriteria(
        val filter: Filter,
        val value: Any
)