package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

class Create(
        private val request: Request,
        private val sensorStatusRepo: ISensorStatusRepository,
        private val id: String
) : Command<Long, Multimap<ErrorTag, String>> {

    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theSensorStatus = request.toEntity()
            sensorStatusRepo.save(theSensorStatus)
            return SimpleResult(theSensorStatus.id, null)
        }
    }

    private fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (gate != 0 || gate != 1 || gate != 2)
                errors.put(ErrorTag.GATE, "Status must be 0, 1, or 2")
            if (proximity != 0 || proximity != 1 || proximity != 2)
                errors.put(ErrorTag.PROXIMITY, "Status must be 0, 1, or 2")
            if (azimuthMotor != 0 || azimuthMotor != 1 || azimuthMotor != 2)
                errors.put(ErrorTag.AZIMUTH_MOTOR, "Status must be 0, 1, or 2")
            if (elevationMotor != 0 || elevationMotor != 1 || elevationMotor != 2)
                errors.put(ErrorTag.ELEVATION_MOTOR, "Status must be 0, 1, or 2")
            if (weatherStation != 0 || weatherStation != 1 || weatherStation != 2)
                errors.put(ErrorTag.WEATHER_STATION, "Status must be 0, 1, or 2")

            if(token.isBlank())
                errors.put(ErrorTag.TOKEN, "Required Field")
            if(token != id)
                errors.put(ErrorTag.TOKEN, "Bad Authorization")
        }

        return if (errors.isEmpty) null else errors
    }

    data class Request(
            val gate: Int,
            val proximity: Int,
            val azimuthMotor: Int,
            val elevationMotor: Int,
            val weatherStation: Int,
            val token: String
    ) : BaseCreateRequest<SensorStatus> {
        override fun toEntity(): SensorStatus {
            return SensorStatus (
                    gate = gate,
                    proximity = proximity,
                    azimuthMotor = azimuthMotor,
                    elevationMotor = elevationMotor,
                    weatherStation = weatherStation
            )
        }
    }
}