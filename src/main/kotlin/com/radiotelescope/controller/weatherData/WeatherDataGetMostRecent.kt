package com.radiotelescope.controller.weatherData

import com.radiotelescope.contracts.weatherData.UserWeatherDataWrapper
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.controller.model.Result
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherDataGetMostRecent (
    private val weatherStatusWrapper: UserWeatherDataWrapper,
    logger: Logger
) : BaseRestController(logger){

    @GetMapping(value = ["/api/weatherData/getMostRecent"])
    fun execute(): Result {
        weatherStatusWrapper.getMostRecent {
            // If the command was a success
            it.success?.let { data ->
                // Create success logs
                logger.createSuccessLog(
                    info = Logger.createInfo(
                        affectedTable = Log.AffectedTable.SENSOR_STATUS,
                        action = "Weather Data Most Recent Retrieval",
                        affectedRecordId = data.id,
                        status = HttpStatus.OK.value()
                    )
                )

                result= Result(data = data)
            }
            // Otherwise, it was an error
            it.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.APPOINTMENT,
                            action = "Weather Data Most Recent Retrieval",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, that means the User did not pass authentication

            // Set the errors depending on if the user was not authenticated or the
            // record does not exist
            logger.createErrorLogs(
                info = Logger.createInfo(
                    affectedTable = Log.AffectedTable.WEATHER_DATA,
                    action = "Weather Data Most Recent Retrieval",
                    affectedRecordId = null,
                    status = if (it.missingRoles != null) HttpStatus.FORBIDDEN.value() else HttpStatus.NOT_FOUND.value()
                ),
                errors = if (it.missingRoles != null) it.toStringMap() else it.invalidResourceId!!
            )

            // Set the errors depending on if the user was not authenticated or the
            // record does not exist
            result = if (it.missingRoles == null) {
                Result(errors = it.invalidResourceId!!, status = HttpStatus.NOT_FOUND)
            }
            // user did not have access to the resource
            else{
                Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
            }
        }
        return result
    }

}