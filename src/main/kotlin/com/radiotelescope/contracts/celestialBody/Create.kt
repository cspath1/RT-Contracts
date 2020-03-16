package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.SolarSystemBodies

/**
 * Override of the [Command] interface used for Celestial Body creation
 *
 * @param request the [Request] object
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class Create(
        private val request: Request,
        private val coordinateRepo: ICoordinateRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist a [CelestialBody] object and,
     * if this particular [CelestialBody] is not within our solar system, a [Coordinate]
     * object as well, signifying the right ascension and declination.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            // Adapt the request into a celestial body (and potentially a coordinate)
            // and return the new record's id
            val celestialBody = request.toEntity()

            if (!SolarSystemBodies.isInSolarSystem(request.name)) {
                val coordinate = request.toCoordinate()
                coordinateRepo.save(coordinate)
                celestialBody.coordinate = coordinate
            }

            celestialBodyRepo.save(celestialBody)

            return SimpleResult(celestialBody.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validation for the
     * [Request]. It will ensure that the name is neither blank nor longer
     * than the database column.
     *
     * From there, it will determine if the name pertains to a celestial body
     * within our solar system. If it is outside of our solar system, it will
     * make sure the right ascension and declination is valid.
     *
     * If any validation fails, it will return a [Multimap] of the errors, otherwise
     * it will return null
     *
     * @return a [Multimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // If blank or greater than the database constraints
            if (name.trim().isBlank()) {
                errors.put(ErrorTag.NAME, "Required field")
            } else if (name.trim().length > 150) {
                errors.put(ErrorTag.NAME, "Celestial Body name must be under 150 characters")
            }

            if (errors.isNotEmpty()) {
                return errors
            }

            // Determine if the Celestial Body entered is within
            // our solar system
            val isInSolarSystem = SolarSystemBodies.isInSolarSystem(name)

            // If the object is not in the solar system
            // right ascension and declination are required
            if (!isInSolarSystem) {
                if (hours == null) {
                    errors.put(ErrorTag.HOURS, "Required field for Celestial Bodies outside of our solar system")
                } else if (hours < 0 || hours >= 24) {
                    errors.put(ErrorTag.HOURS, "Hours must be between 0 and 24")
                }
                if (minutes == null) {
                    errors.put(ErrorTag.MINUTES, "Required field for Celestial Bodies outside of our solar system")
                } else if (minutes < 0 || minutes >= 60) {
                    errors.put(ErrorTag.MINUTES, "Minutes must be between 0 and 59")
                }
                if (declination == null) {
                    errors.put(ErrorTag.DECLINATION, "Required field for Celestial Bodies outside of our solar system")
                } else if (declination < -90 || declination > 90) {
                    errors.put(ErrorTag.DECLINATION, "Declination must be between -90 and 90")
                }
            }
        }

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for [CelestialBody] creation.
     * Implements the [BaseCreateRequest] interface
     */
    data class Request(
            val name: String,
            val hours: Int?,
            val minutes: Int?,
            val declination: Double?
    ) : BaseCreateRequest<CelestialBody> {
        /**
         * Concrete implementation of the [BaseCreateRequest.toEntity] method
         * that returns a [CelestialBody] object
         */
        override fun toEntity(): CelestialBody {
            return CelestialBody(name)
        }

        /**
         * Method that will take the [Request] hours, minutes, seconds, and declination
         * and return a [Coordinate] object.
         *
         * NOTE: This is only called in the event that the request has been validated and
         * a [Coordinate] is required for the particular [CelestialBody], guaranteeing that
         * none of the fields are null
         */
        fun toCoordinate(): Coordinate {
            return Coordinate(
                    hours = hours!!,
                    minutes = minutes!!,
                    rightAscension = Coordinate.hoursMinutesToDegrees(
                            hours = hours,
                            minutes = minutes
                    ),
                    declination = declination!!
            )
        }
    }
}