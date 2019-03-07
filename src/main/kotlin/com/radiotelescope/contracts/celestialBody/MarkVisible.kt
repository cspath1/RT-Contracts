package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository

/**
 * Override of the [Command] interface used for marking a [CelestialBody] as 'visible'
 *
 * @param id the [CelestialBody] id
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class MarkVisible(
        private val id: Long,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method that calls the [validateRequest]
     * method to check for any errors. If errors are returned, it will return those
     * errors in a [SimpleResult]. Otherwise, it will retrieve the [CelestialBody]
     * and set its status to 'Visible'
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theCelestialBody = celestialBodyRepo.findById(id).get()

            theCelestialBody.status = CelestialBody.Status.VISIBLE
            celestialBodyRepo.save(theCelestialBody)

            return SimpleResult(theCelestialBody.id, null)
        }
    }

    /**
     * Handles all constraint checking and validation. If all constraints
     * are met, this will return null. Otherwise, it will return an map of
     * the errors.
     *
     * @return a [HashMultimap] or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (!celestialBodyRepo.existsById(id)) {
            errors.put(ErrorTag.ID, "Celestial Body #$id was not found")
        } else {
            val theCelestialBody = celestialBodyRepo.findById(id).get()

            // Make sure it is not already hidden
            if (theCelestialBody.status == CelestialBody.Status.VISIBLE) {
                errors.put(ErrorTag.STATUS, "Status already marked as 'Visible'")
            }
        }

        return if (errors.isEmpty) null else errors
    }
}