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
class Update (
        private val request: Request,
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
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val newSensorThreshold = Thresholds(Thresholds.Name.valueOf(request.sensorName), request.maximum)

        thresholdsRepo.save(newSensorThreshold)

        return SimpleResult(thresholdsRepo.save(newSensorThreshold), null)
    }

    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // should never happen
            if (thresholdsRepo.findAll().count() == 0)
                errors.put(ErrorTag.ID, "Sensor thresholds not found")

            // check if each threshold below zero
            if (maximum < 0)
                errors.put(ErrorTag.NAME, "Threshold must be higher than 0")
        }

        return errors
    }

    data class Request(
            val sensorName: String,
            val maximum: Double
    )
}