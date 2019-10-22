package com.radiotelescope.contracts.coordinate

import com.radiotelescope.repository.coordinate.Coordinate

/**
 * Data class used to represent a read-only version of the
 * Coordinate entity
 *
 * @param hours the right ascension hours
 * @param minutes the right ascension minutes
 * @param rightAscension the right ascension in degrees
 * @param declination the declination
 */
data class CoordinateInfo(
        val hours: Int,
        val minutes: Int,
        val rightAscension: Double,
        val declination: Double
) {
    /**
     * Secondary constructor that takes a coordinate object
     * to set all fields
     *
     * @param coordinate the Coordinate
     */
    constructor(coordinate: Coordinate) : this(
            hours = coordinate.hours,
            minutes = coordinate.minutes,
            rightAscension = coordinate.rightAscension,
            declination = coordinate.declination
    )
}