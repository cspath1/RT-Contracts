package com.radiotelescope.contracts.weatherData

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
import java.util.*

/**
 * Override of the [Command] interface used to retrieve videos
 * between creation dates
 *
 * @param request the [Request] object
 * @param weatherDataRepo the [IWeatherDataRepository] interface
 */
class ListBetweenCreationDates(
    private val request: ListBetweenCreationDates.Request,
    private val weatherDataRepo: IWeatherDataRepository
) : Command<List<WeatherData>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes it will create a [List] of [WeatherData] objects and
     * return this in the [SimpleResult.success] value.
     *
     * If validation fails, it will will return the errors in a [SimpleResult.error]
     * value.
     */
    override fun execute(): SimpleResult<List<WeatherData>, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if(!errors.isEmpty){
            return SimpleResult(null, errors)
        }

        val list = weatherDataRepo.findVideosCreatedBetweenDates(
                lowerDate = request.lowerDate,
                upperDate = request.upperDate
        )

        return SimpleResult(list, null)
    }

    /**
     * Method responsible for constraint checking and validations that
     * ensures the upperTime is greater than lowerTime
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (upperDate <= lowerDate)
                errors.put(ErrorTag.INSERT_TIMESTAMP, "Upper request time bound cannot be less than or equal to lower request time bound")
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for listing video file records between dates
     */
    data class Request(
            var lowerDate: Date,
            var upperDate: Date
    )
}