package com.radiotelescope.contracts.thresholds

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds

/**
 * Override of the [Command] interface method used to retrieve [Thresholds]
 * information
 *
 * @param thresholdsRepo the [IThresholdsRepository] interface
 */
class Retrieve (
        private val thresholdsRepo: IThresholdsRepository
) : Command<Thresholds, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. It checks the database for
     * the single entry in the thresholds table using the [IThresholdsRepository.findAll]
     * method.
     *
     * If the thresholds entry does not exist (should never happen),
     * it will return an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<Thresholds, Multimap<ErrorTag, String>> {
        if (thresholdsRepo.findAll().first() == null) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Sensor thresholds not found")
            return SimpleResult(null, errors)
        }

        val theThresholds = thresholdsRepo.findAll().first()

        return SimpleResult(theThresholds, null)
    }
}