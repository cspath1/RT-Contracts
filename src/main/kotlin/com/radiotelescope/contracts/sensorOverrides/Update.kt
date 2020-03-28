package com.radiotelescope.contracts.sensorOverrides

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides

/**
 * Override of the [Command] interface method used to retrieve [SensorOverrides]
 * information
 *
 * @param sensorOverridesRepo the [ISensorOverridesRepository] interface
 */
class Update (
        private val request: Request,
        private val sensorOverridesRepo: ISensorOverridesRepository
) : Command<SensorOverrides, Multimap<ErrorTag, String>> {

    /**
     * Override of the [Command] execute method. The method inserts a new
     * record into the sensor_overrides table using [sensorOverridesRepo.save],
     * overwriting the old override status
     *
     * If there are no sensor_overrides (which should never happen),
     * it will return an error in the [SimpleResult].
     */
    override fun execute(): SimpleResult<SensorOverrides, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val newSensorOverride = SensorOverrides(SensorOverrides.Name.valueOf(request.sensorName), request.overridden)

        return SimpleResult(sensorOverridesRepo.save(newSensorOverride), null)
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object.
     *
     * @return a [HashMultimap] of errors or null
     */
    private fun validateRequest(): Multimap<ErrorTag, String>{
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            // should never happen
            if (sensorOverridesRepo.findAll().count() == 0)
                errors.put(ErrorTag.ID, "Sensor overrides not found")

            // check if sensor name is a valid name in the enum
            if (!SensorOverrides.Name.values().map { it.name }.contains(sensorName))
                errors.put(ErrorTag.NAME, "Sensor Name must be a valid sensor")
        }

        return errors
    }

    /**
     * Data class containing the fields necessary to update a sensor override
     *
     * @param sensorName the name of the sensor
     * @param overridden the sensor overridden status
     */
    data class Request(
            val sensorName: String,
            val overridden: Boolean
    )
}