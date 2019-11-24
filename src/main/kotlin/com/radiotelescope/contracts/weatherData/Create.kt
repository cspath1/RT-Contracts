package com.radiotelescope.contracts.weatherData

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.BaseCreateRequest
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.weatherData.WeatherData
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import java.util.*

/**
 * Override of the [Command] interface used for VideoFile creation
 *
 * @param request the [Request] object
 * @param weatherDataRepo the [IWeatherDataRepository] interface
 */
class Create (
    private val request: Request,
    private val weatherDataRepo: IWeatherDataRepository
) : Command<Long, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method. Calls the [validateRequest] method
     * that will handle all constraint checking and validation.
     *
     * If validation passes, it will create and persist the [WeatherData] object and return
     * the id in the [SimpleResult] object.
     *
     * If validation fails, it will return a [SimpleResult] with the errors.
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>>{
        validateRequest()?.let{ return SimpleResult(null, it) } ?: let {
            val theWeatherData = request.toEntity()
            weatherDataRepo.save(theWeatherData)
            return SimpleResult(theWeatherData.id, null)
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
            if (windDirectionStr?.isBlank()!!){
                errors.put(ErrorTag.WIND_DIRECTION_STR, "Required Field")
            }
        }
        return if (errors.isEmpty) null else errors
    }

    /**
     * Data class containing all fields necessary for weather data creation
     * Implements the [BaseCreateRequest] interface
     */

    data class Request (
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
            val heatIndex: Float?,
            val insertTimeStamp: Date?,
            val updateTimeStamp: Date?
    ) : BaseCreateRequest<WeatherData>{
        override fun toEntity(): WeatherData{
            return WeatherData(
                    windSpeed = windSpeed,
                    windDirectionDeg = windDirectionDeg,
                    windDirectionStr = windDirectionStr,
                    outsideTemperatureDegF = outsideTemperatureDegF,
                    insideTemperatureDegF = insideTemperatureDegF,
                    rainRate = rainRate,
                    rainTotal = rainTotal,
                    rainDay = rainDay,
                    rainMonth = rainMonth,
                    barometricPressure = barometricPressure,
                    dewPoint = dewPoint,
                    windChill = windChill,
                    humidity = humidity,
                    heatIndex = heatIndex,
                    insertTimestamp = insertTimeStamp,
                    updateTimestamp = updateTimeStamp
            )
        }
    }
}


