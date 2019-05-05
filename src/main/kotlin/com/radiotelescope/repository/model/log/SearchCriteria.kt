package com.radiotelescope.repository.model.log

import com.radiotelescope.repository.log.Log

/**
 * Data class containing the [Filter] - the field - of a [Log] entity.
 * This is what field will be used to search
 *
 * @param filter the [Filter] enum
 * @param value the Search value
 */
data class SearchCriteria(
    val filter: Filter,
    val value: Any
)
