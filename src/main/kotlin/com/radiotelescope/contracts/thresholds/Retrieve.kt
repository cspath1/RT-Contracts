package com.radiotelescope.contracts.thresholds

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds

/**
 * Override of the [Command] interface method used to retrieve [Thresholds]
 * information for one threshold
 *
 * @param thresholdsRepo the [IThresholdsRepository] interface
 * @param sensorName the name of the sensor threshold to retrieve
 */
class Retrieve (
        private val thresholdsRepo: IThresholdsRepository,
        private val sensorName: String
) : Command<Thresholds, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. It checks the database for
     * the last entry for a given sensor name by executing the
     * [IThresholdsRepository.getMostRecentThresholdByName] method.
     *
     * If the thresholds entry does not exist (should never happen),
     * it will return an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<Thresholds, Multimap<ErrorTag, String>> {
        // should never happen
        if (thresholdsRepo.findAll().count() == 0) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Sensor thresholds not found")
            return SimpleResult(null, errors)
        }

        // https://stackoverflow.com/questions/41844080/kotlin-how-to-check-if-enum-contains-a-given-string-without-messing-with-except
        if (!Thresholds.Name.values().map { it.name }.contains(sensorName)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.NAME, "Incorrect sensor name")
            return SimpleResult(null, errors)
        }

        val theThresholds = thresholdsRepo.getMostRecentThresholdByName(sensorName)
        return SimpleResult(theThresholds, null)
    }
}