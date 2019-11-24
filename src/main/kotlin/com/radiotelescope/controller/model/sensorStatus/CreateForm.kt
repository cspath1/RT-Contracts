package com.radiotelescope.controller.model.sensorStatus

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.sensorStatus.Create
import com.radiotelescope.contracts.sensorStatus.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import java.util.*

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
        val proximity: Int?,
        val azimuthMotor: Int?,
        val elevationMotor: Int?,
        val weatherStation: Int?,
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
                proximity = proximity!!,
                azimuthMotor = azimuthMotor!!,
                elevationMotor = elevationMotor!!,
                weatherStation = weatherStation!!,
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
        if (proximity == null)
            errors.put(ErrorTag.PROXIMITY, "Required Field")
        if (azimuthMotor == null)
            errors.put(ErrorTag.AZIMUTH_MOTOR, "Required Field")
        if (elevationMotor == null)
            errors.put(ErrorTag.ELEVATION_MOTOR, "Required Field")
        if (weatherStation == null)
            errors.put(ErrorTag.WEATHER_STATION, "Required Field")
        if (token == null)
            errors.put(ErrorTag.TOKEN, "Required Field")

        return if (errors.isEmpty) null else errors
    }
}