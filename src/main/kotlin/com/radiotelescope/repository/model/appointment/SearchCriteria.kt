package com.radiotelescope.repository.model.appointment

import com.radiotelescope.repository.appointment.Appointment

/**
 * Data class containing the [Filter] - the search field - for an [Appointment] search
 *
 * @param filter the [Filter] enum
 * @param value the Search value
 */
data class SearchCriteria(
        val filter: Filter,
        val value: Any
)