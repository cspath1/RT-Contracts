package com.radiotelescope.contracts.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus

/**
 * Override of the [Command] interface used for VideoFile creation
 *
 * @param request the [Request] object
 * @param sensorStatusRepo the [ISensorStatusRepository] interface
 * @param id the uuid used to verify control room access
 */
class Create(
        private val request: Request,
        private val sensorStatusRepo: ISensorStatusRepository,
        private val id: String
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [SensorStatus] object and return
     * the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        validateRequest()?.let { return SimpleResult(null, it) } ?: let {
            val theSensorStatus = request.toEntity()
            sensorStatusRepo.save(theSensorStatus)
            return SimpleResult(theSensorStatus.id, null)
        }
    }

    /**
     * Method responsible for constraint checking and validations for the
     * [Request] object.
     *
     * @return a [HashMultimap] of errors or null
     */
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

    /**
     * Data class containing all fields necessary for video file creation.
     * Implements the [BaseCreateRequest] interface
     */
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