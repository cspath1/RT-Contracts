package com.radiotelescope.controller.weatherData


import com.radiotelescope.contracts.weatherData.UserWeatherDataWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.weatherData.ListBetweenCreationDatesForm
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * Rest Controller to handle retrieving a video file path
 *
 * @param logger the [Logger] service
 */
@RestController
class WeatherDataListBetweenCreationDatesController(
        private val weatherDataWrapper: UserWeatherDataWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method in charge of listing the video files created between the
     * lower and upper dates.
     *
     * If the fields in the [ListBetweenCreationDatesForm] are null or invalid,
     * respond with errors. Otherwise, call the [UserWeatherDataWrapper.listBetweenCreationDates]
     * method. If this method returns an [AccessReport], this means that user authentication
     * failed and the method should respond with errors, setting the [Result]'s
     * [HttpStatus] to [HttpStatus.FORBIDDEN].
     *
     * If not, the command object was executed, and was either a success or failure,
     * and the method should respond accordingly based on each scenario.
     */
    @GetMapping(value = ["/api/weather-data/listBetweenCreatedDates"])
    fun execute(@RequestParam("lowerDate") lowerDate: Date,
                @RequestParam("upperDate") upperDate: Date) : Result {

        val form = ListBetweenCreationDatesForm(
                lowerDate = lowerDate,
                upperDate = upperDate
        )

        // If any of the request params are null, respond with errors
        val errors = form.validateRequest()
        if(errors != null) {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.WEATHER_DATA,
                            action = "Weather Data List Between Creation Times",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
            )
            result = Result(errors = errors.toStringMap())
        }
        // Otherwise, call the wrapper method
        else {
            val request = form.toRequest()
            weatherDataWrapper.listBetweenCreationDates(
                    request
            ) { response ->

                // If the command was a success
                response.success?.let { list ->
                    // Create success logs for each retrieval
                    list.forEach { info ->
                        logger.createSuccessLog(
                                info = Logger.createInfo(
                                        Log.AffectedTable.WEATHER_DATA,
                                        action = "Weather Data List Between Times",
                                        affectedRecordId = info.id,
                                        status = HttpStatus.OK.value()
                                )
                        )
                    }

                    result = Result(data = list)
                }
                // If the command was a failure
                response.error?.let { errors ->
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.WEATHER_DATA,
                                    action = "Weather Data List Between Times",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let {
                // If we get here, this means the User did not pass validation
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.WEATHER_DATA,
                                action = "Weather Data List Between Times",
                                affectedRecordId = null,
                                status = HttpStatus.FORBIDDEN.value()
                        ),
                        errors = it.toStringMap()
                )

                result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }

        return result
    }
}