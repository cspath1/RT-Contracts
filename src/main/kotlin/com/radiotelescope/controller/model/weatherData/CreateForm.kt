package com.radiotelescope.controller.model.weatherData

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.contracts.weatherData.Create
import com.radiotelescope.contracts.weatherData.ErrorTag
import java.util.*
/**
 * Create form that takes nullable versions of the [Create.Request] object.
 * It is in charge of making sure these values are not null before adapting it
 * into a [Create.Request] object.
 *
 * @param windSpeed the speed of wind
 * @param windDirectionStr wind direction in strength
 * @param windDirectionDeg wind direction in degrees
 * @param outsideTemperatureDegF outside temp in Fahrenheit
 * @param insideTemperatureDegF inside temp in Fahrenheit
 * @param rainRate rate of rain
 * @param rainTotal rain total
 * @param rainDay rain total for day
 * @param rainMonth rain total for month
 * @param barometricPressure barometric pressure
 * @param dewPoint dew point float
 * @param windChill wind chill float
 * @param humidity humidity level float
 * @param heatIndex heat index float
 */
data class CreateForm(
        val windSpeed: Float?,
        val windDirectionDeg: Float?,
        val windDirectionStr: String?,
        val outsideTemperatureDegF: Float?,
        val insideTemperatureDegF: Float?,
        val rainRate: Float?,
        val rainTotal: Float?,
        val rainDay: Float?,
        val rainMonth: Float?,
        val barometricPressure: Float?,
        val dewPoint: Float?,
        val windChill: Float?,
        val humidity: Float?,
        val heatIndex: Float?
) : BaseForm<Create.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that
     * adapts the form into a [Create.Request] object
     *
     * @return the [Create.Request] object
     */
    override fun toRequest(): Create.Request {
        return Create.Request(
                windSpeed = windSpeed!!,
                windDirectionStr = windDirectionStr!!,
                windDirectionDeg = windDirectionDeg!!,
                outsideTemperatureDegF = outsideTemperatureDegF!!,
                insideTemperatureDegF = insideTemperatureDegF!!,
                rainRate = rainRate!!,
                rainTotal = rainTotal!!,
                rainDay = rainDay!!,
                rainMonth = rainMonth!!,
                barometricPressure = barometricPressure!!,
                dewPoint = dewPoint!!,
                windChill = windChill!!,
                humidity = humidity!!,
                heatIndex = heatIndex!!
        )
    }
    /**
     * Makes sure the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (windSpeed == null)
            errors.put(ErrorTag.WIND_SPEED, "Required Field")
        if (windDirectionStr == null)
            errors.put(ErrorTag.WIND_DIRECTION_STR, "Required Field")
        if (windDirectionDeg== null)
            errors.put(ErrorTag.WIND_DIRECTION_DEG, "Required Field")
        if (outsideTemperatureDegF == null)
            errors.put(ErrorTag.OUTSIDE_TEMPERATURE_DEG_F, "Required Field")
        if(insideTemperatureDegF == null)
            errors.put(ErrorTag.INSIDE_TEMPERATURE_DEG_F, "Required field")
        if(rainRate == null)
            errors.put(ErrorTag.RAIN_RATE, "Required Field")
        if(rainTotal == null)
            errors.put(ErrorTag.RAIN_TOTAL, "Required Field")
        if(rainDay == null)
            errors.put(ErrorTag.RAIN_DAY, "Required Field")
        if(rainMonth == null)
            errors.put(ErrorTag.RAIN_MONTH, "Required Field")
        if(barometricPressure == null)
            errors.put(ErrorTag.BAROMETRIC_PRESSURE, "Required Field")
        if(dewPoint == null)
            errors.put(ErrorTag.DEW_POINT, "Required Field")
        if(windChill == null)
            errors.put(ErrorTag.WIND_CHILL, "Required Field")
        if(humidity == null)
            errors.put(ErrorTag.HUMIDITY, "Required Field")
        if(heatIndex == null)
            errors.put(ErrorTag.HEAT_INDEX, "Required Field")
        return if (errors.isEmpty) null else errors
    }
}