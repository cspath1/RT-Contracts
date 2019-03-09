package com.radiotelescope.contracts.celestialBody

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.celestialBody.CelestialBody

/**
 * Override of the [Command] interface used to retrieve [CelestialBody]
 * information.
 *
 * @param id the request [CelestialBody] id
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 */
class Retrieve(
        private val id: Long,
        private val celestialBodyRepo: ICelestialBodyRepository
) : Command<CelestialBodyInfo, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks to see if
     * the supplied id refers to an existing [CelestialBody] Entity, and
     * if so, it will retrieve it and adapt it into a [CelestialBodyInfo]
     * data class. It will then return this information in a [SimpleResult].
     *
     * If the Celestial Body does not exist, it will return an error
     * in the [SimpleResult]
     */
    override fun execute(): SimpleResult<CelestialBodyInfo, Multimap<ErrorTag, String>> {
        if (!celestialBodyRepo.existsById(id)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Celestial Body Id #$id not found")
            return SimpleResult(null, errors)
        }

        val theCelestialBody = celestialBodyRepo.findById(id).get()
        val theInfo = CelestialBodyInfo(theCelestialBody)
        return SimpleResult(theInfo, null)
    }
}