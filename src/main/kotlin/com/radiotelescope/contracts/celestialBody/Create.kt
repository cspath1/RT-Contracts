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

class Create(
        private val request: Request,
        private val coordinateRepo: ICoordinateRepository,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            // Adapt the request into a celestial body (and potentially a coordinate)
            // and return the new record's id
            val celestialBody = request.toEntity()

            if (!isInSolarSystem(request.name)) {
                val coordinate = request.toCoordinate()
                coordinateRepo.save(coordinate)
                celestialBody.coordinate = coordinate
            }

            celestialBodyRepo.save(celestialBody)

            return SimpleResult(celestialBody.id, null)
        }
    }

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
            val isInSolarSystem = isInSolarSystem(name)

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
                if (seconds == null) {
                    errors.put(ErrorTag.SECONDS, "Required field for Celestial Bodies outside of our solar system")
                } else if (seconds < 0 || seconds >= 60) {
                    errors.put(ErrorTag.SECONDS, "Seconds must be between 0 and 59")
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

    private fun isInSolarSystem(name: String): Boolean {
        return SolarSystemBodies.values().any {
            it.label.toLowerCase().contains(name.trim().toLowerCase()) ||
                    it.label.toLowerCase() == name.trim().toLowerCase()
        }
    }

    data class Request(
            val name: String,
            val hours: Int?,
            val minutes: Int?,
            val seconds: Int?,
            val declination: Double?
    ) : BaseCreateRequest<CelestialBody> {
        override fun toEntity(): CelestialBody {
            return CelestialBody(name)
        }

        // NOTE: This is only called after validation
        // so we can guarantee no values are null
        fun toCoordinate(): Coordinate {
            return Coordinate(
                    hours = hours!!,
                    minutes = minutes!!,
                    seconds = seconds!!,
                    rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds
                    ),
                    declination = declination!!
            )
        }
    }
}