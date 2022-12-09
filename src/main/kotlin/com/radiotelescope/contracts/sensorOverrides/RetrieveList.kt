package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides

/**
 * Override of the [Command] interface method used to retrieve all
 * [SensorOverrides] information
 *
 * @param sensorOverridesRepo the [ISensorOverridesRepository] interface
 */
class RetrieveList (
        private val sensorOverridesRepo: ISensorOverridesRepository
) : Command<List<SensorOverrides>, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. It checks the database for
     * each sensor by by executing the
     * [ISensorOverridesRepository.getMostRecentSensorOverridesByName] method on each
     * sensor name.
     *
     * If the thresholds entries do not exist (should never happen),
     * it will return an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<List<SensorOverrides>, Multimap<ErrorTag, String>> {
        if (sensorOverridesRepo.findAll().count() == 0) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Sensor overrides not found")
            return SimpleResult(null, errors)
        }

        val sensorOverridesList = mutableListOf<SensorOverrides>()

        for (sensor in SensorOverrides.Name.values()) {
            sensorOverridesList.add(sensorOverridesRepo.getMostRecentSensorOverridesByName(sensor.toString()))
        }

        return SimpleResult(sensorOverridesList, null)
    }
}