package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Override of the [Command] interface method used to retrieve [SensorStatus]
 * information
 *
 * @param sensorStatusId the requested Appointment's id
 * @param sensorStatusRepo the [ISensorStatusRepository] interface
 */
class Retrieve (
        private val sensorStatusId: Long,
        private val sensorStatusRepo: ISensorStatusRepository
) : Command<SensorStatus, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. It checks if the supplied id
     * matches with any id in the database through the [ISensorStatusRepository.findById]
     * method.
     *
     * If the sensor status does not exist, it will return an error in the
     * [SimpleResult].
     */
    override fun execute(): SimpleResult<SensorStatus, Multimap<ErrorTag, String>> {
        if(!sensorStatusRepo.existsById(sensorStatusId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Sensor Status Id #$sensorStatusId not found")
            return SimpleResult(null, errors)
        }

        val theSensorStatus = sensorStatusRepo.findById(sensorStatusId).get()

        return SimpleResult(theSensorStatus, null)
    }
}