package com.radiotelescope.repository.model.user

import com.radiotelescope.repository.user.User

/**
 * Data class containing the [Filter] - the field - of a [User] entity.
 * This is what field will be used to search
 *
 * @param filter the [Filter] enum
 * @param value the Search value
 */
data class SearchCriteria(
        val filter: Filter,
        val value: Any
)