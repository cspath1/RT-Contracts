package com.radiotelescope.controller.model.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.sensorStatus.Create
import com.radiotelescope.contracts.sensorStatus.ErrorTag
import com.radiotelescope.controller.model.BaseForm

/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Create.Request] object.
 *
 * @param gate status of the fence gate
 * @param proximity status of the proximity sensor
 * @param azimuthMotor status of the azimuth motor
 * @param elevationMotor status of the elevation motor
 * @param weatherStation status of the weather station
 */
data class CreateForm (
        val gate: Int?,
        val weatherStation: Int?,
        val elevation_abs_encoder : Int?,
        val azimuth_abs_encoder: Int?,
        val el_proximity_0 : Int?,
        val el_proximity_90 : Int?,
        val az_motor_temp_1 : Int?,
        val az_motor_temp_2: Int?,
        val el_motor_temp_1 : Int?,
        val el_motor_temp_2 : Int?,
        val az_accel : Int?,
        val el_accel : Int?,
        val counter_balance_accel: Int?,
        val token: String?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        return Create.Request(
                gate = gate!!,
                weatherStation = weatherStation!!,
                elevation_abs_encoder = elevation_abs_encoder!!,
                azimuth_abs_encoder = azimuth_abs_encoder!!,
                el_proximity_0 = el_proximity_0!!,
                el_proximity_90 = el_proximity_90!!,
                az_motor_temp_1 = az_motor_temp_1!!,
                az_motor_temp_2 = az_motor_temp_2!!,
                el_motor_temp_1 = el_motor_temp_1!!,
                el_motor_temp_2 = el_motor_temp_2!!,
                az_accel = az_accel!!,
                el_accel = el_accel!!,
                counter_balance_accel = counter_balance_accel!!,
                token = token!!
        )
    }

    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (gate == null)
            errors.put(ErrorTag.GATE, "Required Field")
        if (weatherStation == null)
            errors.put(ErrorTag.WEATHER_STATION, "Required Field")
        if (elevation_abs_encoder  == null)
            errors.put(ErrorTag.elevation_abs_encoder , "Required Field")
        if (azimuth_abs_encoder  == null)
            errors.put(ErrorTag.azimuth_abs_encoder , "Required Field")
        if (el_proximity_0  == null)
            errors.put(ErrorTag.el_proximity_0 , "Required Field")
        if (el_proximity_90  == null)
            errors.put(ErrorTag.el_proximity_90 , "Required Field")
        if (az_motor_temp_1  == null)
            errors.put(ErrorTag.az_motor_temp_1 , "Required Field")
        if (az_motor_temp_2 == null)
            errors.put(ErrorTag.az_motor_temp_2, "Required Field")
        if (el_motor_temp_1  == null)
            errors.put(ErrorTag.el_motor_temp_1 , "Required Field")
        if (el_motor_temp_2  == null)
            errors.put(ErrorTag.el_motor_temp_2 , "Required Field")
        if (az_accel  == null)
            errors.put(ErrorTag.az_accel , "Required Field")
        if (el_accel == null)
            errors.put(ErrorTag.el_accel, "Required Field")
        if (counter_balance_accel  == null)
            errors.put(ErrorTag.counter_balance_accel , "Required Field")
        if (token == null)
            errors.put(ErrorTag.TOKEN, "Required Field")

        return if (errors.isEmpty) null else errors
    }
}