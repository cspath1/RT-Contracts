package com.radiotelescope.contracts.thresholds

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds

/**
 * Override of the [Command] interface method used to retrieve all
 * [Thresholds] information
 *
 * @param thresholdsRepo the [IThresholdsRepository] interface
 */
class RetrieveList (
        private val thresholdsRepo: IThresholdsRepository
) : Command<List<Thresholds>, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. It checks the database for
     * each sensor by by executing the
     * [IThresholdsRepository.getMostRecentThresholdByName] method on each
     * sensor name.
     *
     * If the thresholds entries do not exist (should never happen),
     * it will return an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<List<Thresholds>, Multimap<ErrorTag, String>> {
        // should never happen
        if (thresholdsRepo.findAll().count() == 0) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Sensor thresholds not found")
            return SimpleResult(null, errors)
        }

        val sensorThresholdsList = mutableListOf<Thresholds>()

        for (sensor in Thresholds.Name.values()) {
            sensorThresholdsList.add(thresholdsRepo.getMostRecentThresholdByName(sensor.toString()))
        }

        return SimpleResult(sensorThresholdsList, null)
    }
}