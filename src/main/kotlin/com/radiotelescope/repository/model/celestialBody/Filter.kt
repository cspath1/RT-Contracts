package com.radiotelescope.repository.model.celestialBody

import com.radiotelescope.repository.celestialBody.CelestialBody

/**
 * Enum class that acts as a search filter for the [CelestialBody] entity
 *
 * @param field the corresponding entity field
 */
enum class Filter(val field: String) {
    NAME("name");

    companion object {
        fun fromField(field: String): Filter? {
            return if (field == "name") {
                NAME
            } else {
                null
            }
        }
    }
}