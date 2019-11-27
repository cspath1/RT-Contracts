package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

class Retrieve (
        private val sensorStatusId: Long,
        private val sensorStatusRepo: ISensorStatusRepository
) : Command<SensorStatus, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<SensorStatus, Multimap<ErrorTag, String>> {
        if(!sensorStatusRepo.existsById(sensorStatusId)) {
            val errors = HashMultimap.create<ErrorTag, String>()
            errors.put(ErrorTag.ID, "Appointment Id #$sensorStatusId not found")
            return SimpleResult(null, errors)
        }

        val theSensorStatus = sensorStatusRepo.findById(sensorStatusId).get()

        return SimpleResult(theSensorStatus, null)
    }
}