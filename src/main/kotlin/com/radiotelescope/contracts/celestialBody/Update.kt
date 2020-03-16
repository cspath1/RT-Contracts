package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.isNotEmpty
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.SolarSystemBodies

/**
 * Override of the [Command] interface used for updating an existing
 * Celestial Body.
 *
 * @param request the [Request] object
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class Update(
        private val request: Request,
        private val coordinateRepo: ICoordinateRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will update the [CelestialBody] object. Along with this,
     * depending on the scenario, certain things will happen to the [Coordinate] associated:
     *
     * 1.) If the [CelestialBody], prior to being updated, required a [Coordinate], and
     * still does (i.e. it exists outside of our solar system), the [Coordinate] will be
     * updated.
     *
     * 2.) If the [CelestialBody], prior to being updated, did not require a [Coordinate],
     * but now does (i.e. For some reason, it was changed from being within the solar system
     * to being an object outside of our solar system), a new [Coordinate] will be persisted
     * and associated with the [CelestialBody].
     *
     * 3.) If the [CelestialBody], prior to being updated, required a [Coordinate], but now
     * does not, the existing [Coordinate] will be disconnected from the [CelestialBody] and
     * will be deleted.
     *
     * 4.) If the [CelestialBody], prior to being updated, did not require a [Coordinate],
     * and still doesn't, nothing will happen.
     *
     * However, if validation fails, it will simply return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            // Adapt the request into an update celestial body record.
            val theOriginalCelestialBody = celestialBodyRepo.findById(request.id).get()
            val theUpdatedCelestialBody = request.updateEntity(theOriginalCelestialBody)

            // Determine if the coordinate needs updated
            if (!SolarSystemBodies.isInSolarSystem(theUpdatedCelestialBody.name)) {
                // If there is a coordinate, it should be updated
                theUpdatedCelestialBody.coordinate?.let {
                    theUpdatedCelestialBody.coordinate = request.updateCoordinate(it)
                } ?: let {
                    // Otherwise, a new record will be created
                    theUpdatedCelestialBody.coordinate = request.toCoordinate()
                }

                // The coordinate is guaranteed not to be null here
                coordinateRepo.save(theUpdatedCelestialBody.coordinate!!)
            } else {
                // Delete associated coordinate if one exists
                theUpdatedCelestialBody.coordinate?.let {
                    val theCoordinate = it
                    theUpdatedCelestialBody.coordinate = null
                    coordinateRepo.delete(theCoordinate)
                }
            }

            // Persist the changes to the Celestial Body
            celestialBodyRepo.save(theUpdatedCelestialBody)

            return SimpleResult(theUpdatedCelestialBody.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validation for the
     * [Request]. It will ensure that id refers to an existing record. It
     * will then ensure that the name is neither blank nor longer than the
     * database column.
     *
     * From there, it will determine if the [Request.name] refers to a celestial
     * body within our solar system. If it is not inside of our solar system, this
     * means that the right ascension and declination will need validated.
     *
     * If any validation fails, it will return a [Multimap] of the errors,
     * otherwise it will return null.
     *
     * @return a [Multimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // Make sure the
            if (!celestialBodyRepo.existsById(id)) {
                errors.put(ErrorTag.ID, "Celestial Body #$id was not found")
            }

            // If blank or greater than the database constraints
            if (name.trim().isBlank()) {
                errors.put(ErrorTag.NAME, "Required field")
            } else if (name.trim().length > 150) {
                errors.put(ErrorTag.NAME, "Celestial Body name must be under 150 characters")
            }

            // Return here if there are any errors
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
     * Data class containing all fields necessary for updating a [CelestialBody] object.
     * Implements the [BaseUpdateRequest] interface.
     *
     * @param id the [CelestialBody] id
     * @param name the [CelestialBody] name
     * @param hours the [Coordinate] hours
     * @param minutes the [Coordinate] minutes
     * @param seconds the [Coordinate] seconds
     * @param declination the [Coordinate] declination
     */
    data class Request(
            var id: Long,
            val name: String,
            val hours: Int?,
            val minutes: Int?,
            val declination: Double?
    ) : BaseUpdateRequest<CelestialBody> {
        /**
         * Concrete implementation of the [BaseUpdateRequest.updateEntity] method
         * that will return an updated [CelestialBody]
         *
         * @param entity the [CelestialBody]
         * @return an update [CelestialBody] object
         */
        override fun updateEntity(entity: CelestialBody): CelestialBody {
            entity.name = name

            return entity
        }

        /**
         * Method that will take the [Request] hours, minutes, seconds, and declination
         * and return an updated [Coordinate] object
         *
         * NOTE: This is only called when a [CelestialBody] requires a Coordinate, meaning
         * it is guaranteed that the values will not be null when this is called
         *
         * @param coordinate the [Coordinate]
         * @return the updated coordinate
         */
        fun updateCoordinate(coordinate: Coordinate): Coordinate {
            coordinate.hours = hours!!
            coordinate.minutes = minutes!!
            coordinate.rightAscension = Coordinate.hoursMinutesToDegrees(
                    hours = hours,
                    minutes = minutes
            )
            coordinate.declination = declination!!

            return coordinate
        }

        /**
         * Method that will take the [Request] hours, minutes, seconds, and declination
         * and return a new [Coordinate] object.
         *
         * Realistically, this should never happen, but should be accounted for just in case it does.
         * (i.e. Updating a Celestial Body from 'The Sun' to 'Crab Nebula' will result in this case)
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