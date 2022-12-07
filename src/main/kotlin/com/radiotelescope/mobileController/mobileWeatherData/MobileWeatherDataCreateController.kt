package com.radiotelescope.controller.weatherData


import com.radiotelescope.contracts.weatherData.Create
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.controller.model.weatherData.CreateForm
import com.radiotelescope.mobileContracts.mobileWeatherData.UserMobileWeatherDataWrapper
import com.radiotelescope.mobileController.mobileModel.MobileWeatherData.MobileCreateForm
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody

/**
 * REST Controller to handle Video File creation
 *
 * @param weatherDataWrapper the [UserWeatherDataWrapper]
 * @param logger the [Logger] service
 */
@RestController
class MobileWeatherDataCreateController(
    private val weatherDataWrapper: UserMobileWeatherDataWrapper,
    logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CreateForm]
     * into a [Create.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserWeatherDataWrapper.create] method.
     * Based on the result of this call, the method will either respond with
     * the data or the errors from the method call.
     */
    @PostMapping(value = ["/api/mobileWeather-data"])
    fun execute(@RequestBody form: MobileCreateForm): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs

            logger.createErrorLogs(
                info = Logger.createInfo(
                    affectedTable = Log.AffectedTable.WEATHER_DATA,
                    action = "Weather Data Creation",
                    affectedRecordId = null,
                    status = HttpStatus.BAD_REQUEST.value()
                ),
                errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        }?:
        // Otherwise, execute the wrapper command
        let {
            val response = weatherDataWrapper.create(
                request = form.toRequest()
            ).execute()
            // If the command was a success
            response.success?.let { data ->
                // Create success log
                logger.createSuccessLog(
                    info = Logger.createInfo(
                        affectedTable = Log.AffectedTable.WEATHER_DATA,
                        action = "Mobile Weather Data Creation",
                        affectedRecordId = data,
                        status = HttpStatus.OK.value()
                    )
                )
                result = Result(data = data)
            }
            // Otherwise, there was an error
            response.error?.let { errors ->
                // Create error logs
                logger.createErrorLogs(
                    info = Logger.createInfo(
                        affectedTable = Log.AffectedTable.WEATHER_DATA,
                        action = "Mobile Weather Data Creation",
                        affectedRecordId = null,
                        status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }
        return result
    }
}