package com.radiotelescope.contracts.coordinate

import com.radiotelescope.repository.coordinate.Coordinate

/**
 * Data class used to represent a read-only version of the
 * Coordinate entity
 *
 * @param hours the right ascension hours
 * @param minutes the right ascension minutes
 * @param seconds the right ascension seconds
 * @param rightAscension the right ascension in degrees
 * @param declination the declination
 */
data class CoordinateInfo(
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
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
            seconds = coordinate.seconds,
            rightAscension = coordinate.rightAscension,
            declination = coordinate.declination
    )
}