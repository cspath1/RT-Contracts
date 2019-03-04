package com.radiotelescope.controller.admin.celestialBody

import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.contracts.celestialBody.Create
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.model.celestialBody.CreateForm
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.security.AccessReport
import com.radiotelescope.repository.log.Log
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller to handle celestial body creation
 *
 * @param celestialBodyWrapper the [UserCelestialBodyWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CelestialBodyCreateController(
        private val celestialBodyWrapper: UserCelestialBodyWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of adapting the [CreateForm] into
     * a [Create.Request] after ensuring no fields are null. If any are,
     * it will instead respond with errors.
     *
     * Otherwise, it will execute the [UserCelestialBodyWrapper.create] method.
     * If this method returns an [AccessReport], this means the user did not pass
     * authentication and the method will respond with errors.
     *
     * Otherwise, the [Create] command was executed, and the controller will check
     * whether this command was a success or not, responding accordingly
     */
    @CrossOrigin(value = ["http://localhost:8081"])
    @PostMapping(value = ["/api/celestial-body"])
    fun execute(@RequestBody form: CreateForm): Result {
        form.validateRequest()?.let {
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                            action = "Celestial Body Creation",
                            affectedRecordId = null,
                            status = HttpStatus.BAD_REQUEST.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap())
        } ?:
        // Otherwise execute the wrapper method
        let {
            celestialBodyWrapper.create(
                    request = form.toRequest()
            ) { simpleResult ->
                // If the command was a success
                simpleResult.success?.let { id ->
                    // Create a success log
                    logger.createSuccessLog(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                    action = "Celestial Body Creation",
                                    affectedRecordId = id,
                                    status = HttpStatus.OK.value()
                            )
                    )

                    result = Result(data = id)
                }
                // Otherwise it was a failure
                simpleResult.error?.let { error ->
                    // Create error logs
                    logger.createErrorLogs(
                            info = Logger.createInfo(
                                    affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                    action = "Celestial Body Creation",
                                    affectedRecordId = null,
                                    status = HttpStatus.BAD_REQUEST.value()
                            ),
                            errors = error.toStringMap()
                    )

                    result = Result(errors = error.toStringMap())
                }
            }?.let {
                // If we get here, this means the user did not pass authentication
                // Create error logs
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                action = "Celestial Body Creation",
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