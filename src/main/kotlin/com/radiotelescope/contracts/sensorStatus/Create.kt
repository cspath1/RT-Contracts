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
 * @param uuid the uuid used to verify control room access
 * @param profile the user profile
 */
class Create(
        private val request: Request,
        private val sensorStatusRepo: ISensorStatusRepository,
        private val uuid: String,
        private val profile: String
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
            if (profile != "LOCAL")
                errors.put(ErrorTag.TOKEN, "Bad Authorization: Profile must be LOCAL")

            if (gate != 0 && gate != 1 && gate != 2)
                errors.put(ErrorTag.GATE, "Status must be 0, 1, or 2")
            if (weatherStation != 0 && weatherStation != 1 && weatherStation != 2)
                errors.put(ErrorTag.WEATHER_STATION, "Status must be 0, 1, or 2")
            if (elevation_abs_encoder  != 0 && elevation_abs_encoder  != 1 && elevation_abs_encoder  != 2)
                errors.put(ErrorTag.elevation_abs_encoder , "Status must be 0, 1, or 2")
            if (azimuth_abs_encoder   != 0 && azimuth_abs_encoder   != 1 && azimuth_abs_encoder   != 2)
                errors.put(ErrorTag.azimuth_abs_encoder  , "Status must be 0, 1, or 2")
            if (el_proximity_0   != 0 && el_proximity_0   != 1 && el_proximity_0   != 2)
                errors.put(ErrorTag.el_proximity_0  , "Status must be 0, 1, or 2")
            if (el_proximity_90   != 0 && el_proximity_90   != 1 && el_proximity_90   != 2)
                errors.put(ErrorTag.el_proximity_90  , "Status must be 0, 1, or 2")
            if (az_motor_temp_1   != 0 && az_motor_temp_1   != 1 && az_motor_temp_1   != 2)
                errors.put(ErrorTag.az_motor_temp_1  , "Status must be 0, 1, or 2")
            if (az_motor_temp_2   != 0 && az_motor_temp_2   != 1 && az_motor_temp_2   != 2)
                errors.put(ErrorTag.az_motor_temp_2  , "Status must be 0, 1, or 2")
            if (el_motor_temp_1  != 0 && el_motor_temp_1  != 1 && el_motor_temp_1  != 2)
                errors.put(ErrorTag.el_motor_temp_1 , "Status must be 0, 1, or 2")
            if (el_motor_temp_2  != 0 && el_motor_temp_2  != 1 && el_motor_temp_2  != 2)
                errors.put(ErrorTag.el_motor_temp_2 , "Status must be 0, 1, or 2")
            if (az_accel  != 0 && az_accel  != 1 && az_accel  != 2)
                errors.put(ErrorTag.az_accel , "Status must be 0, 1, or 2")
            if (el_accel   != 0 && el_accel   != 1 && el_accel   != 2)
                errors.put(ErrorTag.el_accel  , "Status must be 0, 1, or 2")
            if (counter_balance_accel   != 0 && counter_balance_accel   != 1 && counter_balance_accel   != 2)
                errors.put(ErrorTag.counter_balance_accel  , "Status must be 0, 1, or 2")

            if(token.isBlank())
                errors.put(ErrorTag.TOKEN, "Required Field")
            if(token != uuid)
                errors.put(ErrorTag.TOKEN, "Bad Authorization: Incorrect ID")
        }

        print(errors)

        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for video file creation.
     * Implements the [BaseCreateRequest] interface
     */
    data class Request(
            val gate: Int,
            val weatherStation: Int,
            val elevation_abs_encoder: Int,
            val azimuth_abs_encoder : Int,
            val el_proximity_0 : Int,
            val el_proximity_90 : Int,
            val az_motor_temp_1 : Int,
            val az_motor_temp_2 : Int,
            val el_motor_temp_1: Int,
            val el_motor_temp_2: Int,
            val az_accel : Int,
            val el_accel : Int,
            val counter_balance_accel : Int,
            val token: String
    ) : BaseCreateRequest<SensorStatus> {
        override fun toEntity(): SensorStatus {
            return SensorStatus (
                    gate = gate,
                    weatherStation = weatherStation,
                    elevation_abs_encoder = elevation_abs_encoder,
                    azimuth_abs_encoder = azimuth_abs_encoder ,
                    el_proximity_0 = el_proximity_0 ,
                    el_proximity_90 = el_proximity_90 ,
                    az_motor_temp_1 = az_motor_temp_1 ,
                    az_motor_temp_2 = az_motor_temp_2 ,
                    el_motor_temp_1 = el_motor_temp_1 ,
                    el_motor_temp_2 = el_motor_temp_2 ,
                    az_accel = az_accel ,
                    el_accel = el_accel ,
                    counter_balance_accel = counter_balance_accel
            )
        }
    }
}