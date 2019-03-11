package com.radiotelescope.controller.admin.celestialBody

import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.contracts.celestialBody.Update
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.celestialBody.UpdateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle updating a celestial body
 *
 * @param celestialBodyWrapper the [UserCelestialBodyWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CelestialBodyUpdateController(
        private val celestialBodyWrapper: UserCelestialBodyWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [UpdateForm]
     * into a [Update.Request] after ensuring no fields are null. If
     * any are, it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserCelestialBodyWrapper.update]
     * method. If this method returns as [AccessReport], the user
     * was not authenticated. If not, this means the [Update] method
     * was executed, and the controller should respond based on if the
     * command was a success or not.
     */
    @PutMapping(value = ["/api/celestial-bodies/{celestialBodyId}"])
    fun execute(@PathVariable("celestialBodyId") id: Long,
                @RequestBody form: UpdateForm
    ): Result {
        // If the form validation fails, respond with errors
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                            action = "Celestial Body Update",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?: let {
            // Otherwise call the factory method
            val request = form.toRequest()

            // Set the id to the path variable
            request.id = id

            celestialBodyWrapper.update(
                    request = request
            ) { response ->
                // If the command was a success
                response.success?.let { id ->
                    // Create success logs
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                    action = "Celestial Body Update",
                                    affectedRecordId = id,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    result = Result(data = id)
                }
                // Otherwise it was a failure
                response.error?.let { errors ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                    action = "Celestial Body Update",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = errors.toStringMap()
                    )

                    result = Result(errors = errors.toStringMap())
                }
            }?.let {
                // If we get here, that means the user did not pass authentication
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                action = "Celestial Body Update",
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