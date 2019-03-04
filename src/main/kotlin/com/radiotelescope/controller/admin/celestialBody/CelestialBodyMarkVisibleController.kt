package com.radiotelescope.controller.admin.celestialBody

import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.contracts.celestialBody.MarkVisible
import com.radiotelescope.controller.BaseRestController
import com.radiotelescope.controller.model.Result
import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.log.Log
import com.radiotelescope.security.AccessReport
import com.radiotelescope.toStringMap
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller used to mark a celestial body as visible
 * for users going to create an appointment
 *
 * @param celestialBodyWrapper the [UserCelestialBodyWrapper]
 * @param logger the [Logger] service
 */
@RestController
class CelestialBodyMarkVisibleController(
        private val celestialBodyWrapper: UserCelestialBodyWrapper,
        logger: Logger
) : BaseRestController(logger) {
    /**
     * Execute method that is in charge of taking the celestialBodyId [PathVariable]
     * and executing the [UserCelestialBodyWrapper.markVisible] method. If this method
     * returns an [AccessReport], this means the user did not pass authentication and it
     * should respond with errors.
     *
     * Otherwise, this means the [MarkVisible] command was executed, and the controller
     * should respond to the client based on if the command was a success or not
     */
    @PutMapping(value = ["/api/celestial-bodies/{celestialBodyId}/show"])
    fun execute(@PathVariable("celestialBodyId") id: Long): Result {
        celestialBodyWrapper.markVisible(id) {
            // If the command was a success
            it.success?.let { id ->
                // Create success log
                logger.createSuccessLog(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                action = "Celestial Body Mark Visible",
                                affectedRecordId = id,
                                status = HttpStatus.OK.value()
                        )
                )

                result = Result(data = id)
            }
            // Otherwise it was a failure
            it.error?.let { errors ->
                logger.createErrorLogs(
                        info = Logger.createInfo(
                                affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                                action = "Celestial Body Mark Visible",
                                affectedRecordId = null,
                                status = HttpStatus.BAD_REQUEST.value()
                        ),
                        errors = errors.toStringMap()
                )

                result = Result(errors = errors.toStringMap())
            }
        }?.let {
            // If we get here, this means the user did not pass validation
            // Create error logs
            logger.createErrorLogs(
                    info = Logger.createInfo(
                            affectedTable = Log.AffectedTable.CELESTIAL_BODY,
                            action = "Celestial Body Mark Hidden",
                            affectedRecordId = null,
                            status = HttpStatus.FORBIDDEN.value()
                    ),
                    errors = it.toStringMap()
            )

            result = Result(errors = it.toStringMap(), status = HttpStatus.FORBIDDEN)
        }

        return result
    }
}