package com.radiotelescope.contracts.thresholds

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds

class RetrieveList (
        private val thresholdsRepo: IThresholdsRepository
) : Command<List<Thresholds>, Multimap<ErrorTag, String>> {

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