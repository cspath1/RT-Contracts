package com.radiotelescope.contracts.coordinate

import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.repository.coordinate.Coordinate

/**
 * Data class containing all fields necessary for Coordinate creation.
 * Implements the [BaseCreateRequest] interface
 */
data class CoordinateRequest(
        val hours: Int,
        val minutes: Int,
        val declination: Double
) : BaseCreateRequest<Coordinate> {
    /**
     * Concrete implementation of the [BaseCreateRequest.toEntity] method
     * that returns a Coordinate object
     */
    override fun toEntity(): Coordinate {
        return Coordinate(
                hours = hours,
                minutes = minutes,
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = hours,
                        minutes = minutes
                ),
                declination = declination
        )
    }
}