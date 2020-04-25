package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig

/**
 * Override of the [Command] interface method used to retrieve [SpectracyberConfig]
 * information
 *
 * @param spectracyberConfigId the requested Appointment's id
 * @param spectracyberConfigRepo the [ISpectracyberConfigRepository] interface
 */
class Retrieve (
        private val spectracyberConfigId: Long,
        private val spectracyberConfigRepo: ISpectracyberConfigRepository
) : Command<SpectracyberConfig, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. It checks if the supplied id
     * matches with any id in the database through the [ISpectracyberConfigRepository.findById]
     * method.
     *
     * If the sensor status does not exist, it will return an error in the
     * [SimpleResult].
     */
    override fun execute(): SimpleResult<SpectracyberConfig, Multimap<ErrorTag, String>> {
        if(!spectracyberConfigRepo.existsById(spectracyberConfigId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Spectracyber Id #$spectracyberConfigId not found")
            return SimpleResult(null, errors)
        }

        val theSpectracyberConfig = spectracyberConfigRepo.findById(spectracyberConfigId).get()

        return SimpleResult(theSpectracyberConfig, null)
    }
}