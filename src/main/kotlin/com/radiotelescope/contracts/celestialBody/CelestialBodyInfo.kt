package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.repository.celestialBody.CelestialBody

/**
 * Data class representing a read-only model of the
 * [CelestialBody] Entity.
 *
 * @param id the Celestial Body's id
 * @param name the Celestial Body's name
 * @param hours the Celestial Body's Coordinate hours value
 * @param minutes the Celestial Body's Coordinate minutes value
 * @param declination the Celestial Body's Coordinate declination value
 */
class CelestialBodyInfo(
        val id: Long,
        val name: String,
        val hours: Int?,
        val minutes: Int?,
        val declination: Double?
) {
    /**
     * Secondary constructor that takes a [CelestialBody] object
     * to set all fields
     *
     * @param celestialBody the Celestial Body
     */
    constructor(celestialBody: CelestialBody) : this(
            id  = celestialBody.id,
            name = celestialBody.name,
            hours = celestialBody.coordinate?.hours,
            minutes = celestialBody.coordinate?.minutes,
            declination = celestialBody.coordinate?.declination
    )
}